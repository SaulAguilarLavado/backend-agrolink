package com.proy.utp.backend_agrolink.domain.service;

import com.proy.utp.backend_agrolink.domain.Order;
import com.proy.utp.backend_agrolink.domain.dto.OrderItem;
import com.proy.utp.backend_agrolink.domain.dto.OrderRequest;
import com.proy.utp.backend_agrolink.persistance.crud.PedidoCrudRepository;
import com.proy.utp.backend_agrolink.persistance.crud.ProductoCrudRepository;
import com.proy.utp.backend_agrolink.persistance.crud.TransaccionCrudRepository;
import com.proy.utp.backend_agrolink.persistance.entity.DetallePedido;
import com.proy.utp.backend_agrolink.persistance.entity.Pedido;
import com.proy.utp.backend_agrolink.persistance.entity.Producto;
import com.proy.utp.backend_agrolink.persistance.entity.Transaccion;
import com.proy.utp.backend_agrolink.persistance.entity.Usuario;
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

    private static final Set<String> VALID_STATUSES = Set.of("PENDIENTE", "CONFIRMADO", "ENVIADO", "COMPLETADO", "RECHAZADO");

    public OrderService(
            PedidoCrudRepository pedidoRepository,
            ProductoCrudRepository productoRepository,
            TransaccionCrudRepository transaccionRepository,
            OrderMapper orderMapper,
            UserMapper userMapper,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.transaccionRepository = transaccionRepository;
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
        this.authenticatedUserService = authenticatedUserService;
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

        if ("CONFIRMADO".equals(upperNewStatus) && !"CONFIRMADO".equalsIgnoreCase(oldStatus)) {
            createTransactionsForOrder(pedido);
        }

        pedido.setEstado(upperNewStatus);
        Pedido updatedPedido = pedidoRepository.save(pedido);

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
}