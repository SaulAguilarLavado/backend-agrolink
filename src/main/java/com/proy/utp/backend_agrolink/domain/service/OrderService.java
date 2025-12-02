package com.proy.utp.backend_agrolink.domain.service;

import com.proy.utp.backend_agrolink.domain.Order;
import com.proy.utp.backend_agrolink.domain.dto.OrderItem;
import com.proy.utp.backend_agrolink.domain.dto.OrderRequest;
import com.proy.utp.backend_agrolink.persistance.crud.NotificacionCrudRepository;
import com.proy.utp.backend_agrolink.persistance.crud.PedidoCrudRepository;
import com.proy.utp.backend_agrolink.persistance.crud.ProductoCrudRepository;
import com.proy.utp.backend_agrolink.persistance.crud.TransaccionCrudRepository;
import com.proy.utp.backend_agrolink.persistance.entity.*;
import com.proy.utp.backend_agrolink.persistance.mapper.OrderMapper;
import com.proy.utp.backend_agrolink.persistance.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final PedidoCrudRepository pedidoRepository;
    private final ProductoCrudRepository productoRepository;
    private final TransaccionCrudRepository transaccionRepository;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final AuthenticatedUserService authenticatedUserService;
    private final NotificacionCrudRepository notificacionRepository;
    private final NotificationService notificationService;

    private static final Set<String> VALID_STATUSES = Set.of("PENDIENTE", "CONFIRMADO", "ENVIADO", "COMPLETADO", "RECHAZADO");

    public OrderService(
            PedidoCrudRepository pedidoRepository,
            ProductoCrudRepository productoRepository,
            TransaccionCrudRepository transaccionRepository,
            OrderMapper orderMapper,
            UserMapper userMapper,
            AuthenticatedUserService authenticatedUserService,
            NotificacionCrudRepository notificacionRepository,
            NotificationService notificationService
    ) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.transaccionRepository = transaccionRepository;
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
        this.authenticatedUserService = authenticatedUserService;
        this.notificacionRepository = notificacionRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Order createOrder(OrderRequest request) {
        var buyerDomain = authenticatedUserService.getAuthenticatedUser();
        Usuario buyerEntity = userMapper.toUsuario(buyerDomain);

        Pedido pedido = new Pedido();
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado("PENDIENTE");
        pedido.setComprador(buyerEntity);
        pedido.setTotal(BigDecimal.ZERO);

        List<DetallePedido> detalleEntities = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : request.getItems()) {
            Producto producto = productoRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getProductId()));

            if (producto.getStockDisponible() < item.getQuantity()) {
                throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
            }

            producto.setStockDisponible(producto.getStockDisponible() - item.getQuantity());

            DetallePedido det = new DetallePedido();
            det.setProducto(producto);
            det.setCantidad(item.getQuantity());
            det.setPrecioUnitario(producto.getPrecioPorUnidad());
            det.setPedido(pedido);
            detalleEntities.add(det);
            total = total.add(producto.getPrecioPorUnidad().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        pedido.setTotal(total);
        pedido.setDetalles(detalleEntities);

        Pedido saved = pedidoRepository.save(pedido);

        // Notificar a todos los administradores sobre el nuevo pedido
        notificationService.notifyAdmins(
                "Nuevo pedido #" + saved.getId() + " pendiente de " +
                        buyerDomain.getName() + " " + buyerDomain.getLastname() +
                        " por un total de $" + total.setScale(2)
        );

        return orderMapper.toOrder(saved);
    }

    public Order getOrderById(Long orderId) {
        Pedido pedido = pedidoRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + orderId));
        return orderMapper.toOrder(pedido);
    }

    public List<Order> getAllOrders() {
        return orderMapper.toOrders(pedidoRepository.findAll());
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, String newStatus) {
        String upperNewStatus = newStatus.toUpperCase();
        if (!VALID_STATUSES.contains(upperNewStatus)) {
            throw new IllegalArgumentException("Estado de pedido no válido: " + newStatus);
        }

        Pedido pedido = pedidoRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + orderId));

        String oldStatus = pedido.getEstado();

        if ("RECHAZADO".equals(upperNewStatus) && !"RECHAZADO".equalsIgnoreCase(oldStatus)) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                Producto producto = detalle.getProducto();
                producto.setStockDisponible(producto.getStockDisponible() + detalle.getCantidad());
            }
        }

        // 🔥 MODIFICACIÓN AQUÍ: Crear transacciones tanto en CONFIRMADO como en COMPLETADO
        if (("CONFIRMADO".equals(upperNewStatus) || "COMPLETADO".equals(upperNewStatus))
                && !upperNewStatus.equalsIgnoreCase(oldStatus)) {

            // Verificar si ya existen transacciones para este pedido
            List<Transaccion> existingTransactions = transaccionRepository.findByPedidoId(pedido.getId());

            // Solo crear si no existen transacciones previas
            if (existingTransactions.isEmpty()) {
                createTransactionsForOrder(pedido);
            }
        }

        pedido.setEstado(upperNewStatus);
        Pedido updatedPedido = pedidoRepository.save(pedido);

        // Notificar al comprador sobre el cambio de estado
        notifyBuyerAboutStatusChange(updatedPedido, upperNewStatus);

        // Si el pedido se completó, notificar a los agricultores
        if ("COMPLETADO".equals(upperNewStatus)) {
            notifyFarmersAboutCompletedOrder(updatedPedido);
        }

        return orderMapper.toOrder(updatedPedido);
    }

    private void createTransactionsForOrder(Pedido pedido) {
        Map<Usuario, List<DetallePedido>> detailsByFarmer = pedido.getDetalles().stream()
                .collect(Collectors.groupingBy(detalle -> detalle.getProducto().getAgricultor()));

        for (Map.Entry<Usuario, List<DetallePedido>> entry : detailsByFarmer.entrySet()) {
            Usuario vendedor = entry.getKey();
            List<DetallePedido> farmerDetails = entry.getValue();

            BigDecimal subTotal = farmerDetails.stream()
                    .map(detalle -> detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Transaccion transaccion = new Transaccion();
            transaccion.setPedido(pedido);
            transaccion.setVendedor(vendedor);
            transaccion.setComprador(pedido.getComprador());
            transaccion.setMontoTotal(subTotal);
            transaccion.setFechaTransaccion(LocalDateTime.now());

            transaccionRepository.save(transaccion);

            Notificacion notif = new Notificacion();
            notif.setUsuario(vendedor);
            notif.setMensaje("Has recibido un nuevo pedido (#" + pedido.getId() + ") por un monto de $" + subTotal.setScale(2));
            notificacionRepository.save(notif);
        }
    }

    /**
     * Obtiene todos los pedidos realizados por el usuario actualmente autenticado.
     * @return Una lista de pedidos del comprador.
     */
    public List<Order> getOrdersForAuthenticatedUser() {
        var buyerDomain = authenticatedUserService.getAuthenticatedUser();
        List<Pedido> pedidos = pedidoRepository.findByCompradorId(buyerDomain.getUserId());
        return orderMapper.toOrders(pedidos);
    }

    /**
     * Notifica al comprador sobre el cambio de estado del pedido
     */
    private void notifyBuyerAboutStatusChange(Pedido pedido, String newStatus) {
        String mensaje;
        switch (newStatus) {
            case "CONFIRMADO":
                mensaje = "Tu pedido #" + pedido.getId() + " ha sido confirmado y está siendo procesado.";
                break;
            case "ENVIADO":
                mensaje = "Tu pedido #" + pedido.getId() + " ha sido enviado y está en camino.";
                break;
            case "COMPLETADO":
                mensaje = "Tu pedido #" + pedido.getId() + " ha sido completado exitosamente.";
                break;
            case "RECHAZADO":
                mensaje = "Tu pedido #" + pedido.getId() + " ha sido rechazado. Contacta con soporte para más información.";
                break;
            default:
                mensaje = "El estado de tu pedido #" + pedido.getId() + " ha cambiado a: " + newStatus;
        }

        notificationService.createNotification(pedido.getComprador(), mensaje);
    }

    /**
     * Notifica a los agricultores cuando se completa un pedido que incluye sus productos
     */
    private void notifyFarmersAboutCompletedOrder(Pedido pedido) {
        Map<Usuario, List<DetallePedido>> detailsByFarmer = pedido.getDetalles().stream()
                .collect(Collectors.groupingBy(detalle -> detalle.getProducto().getAgricultor()));

        for (Map.Entry<Usuario, List<DetallePedido>> entry : detailsByFarmer.entrySet()) {
            Usuario agricultor = entry.getKey();
            List<DetallePedido> detalles = entry.getValue();

            StringBuilder productos = new StringBuilder();
            for (int i = 0; i < detalles.size(); i++) {
                DetallePedido det = detalles.get(i);
                productos.append(det.getProducto().getNombre())
                        .append(" (x").append(det.getCantidad()).append(")");
                if (i < detalles.size() - 1) {
                    productos.append(", ");
                }
            }

            String mensaje = "El pedido #" + pedido.getId() + " que incluye tus productos: " +
                    productos.toString() + " ha sido completado por " +
                    pedido.getComprador().getNombre() + " " + pedido.getComprador().getApellido();

            notificationService.createNotification(agricultor, mensaje);
        }
    }
}