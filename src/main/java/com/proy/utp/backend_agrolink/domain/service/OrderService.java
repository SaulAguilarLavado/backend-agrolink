package com.proy.utp.backend_agrolink.domain.service;

import com.proy.utp.backend_agrolink.domain.Order;
import com.proy.utp.backend_agrolink.domain.dto.OrderItem;
import com.proy.utp.backend_agrolink.domain.dto.OrderRequest;
import com.proy.utp.backend_agrolink.persistance.crud.DetallePedidoCrudRepository;
import com.proy.utp.backend_agrolink.persistance.crud.PedidoCrudRepository;
import com.proy.utp.backend_agrolink.persistance.crud.ProductoCrudRepository;
import com.proy.utp.backend_agrolink.persistance.entity.DetallePedido;
import com.proy.utp.backend_agrolink.persistance.entity.Pedido;
import com.proy.utp.backend_agrolink.persistance.entity.Producto;
import com.proy.utp.backend_agrolink.persistance.entity.Usuario;
import com.proy.utp.backend_agrolink.persistance.mapper.OrderMapper;
import com.proy.utp.backend_agrolink.persistance.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- IMPORTANTE

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set; // <-- IMPORTANTE

@Service
public class OrderService {

    private final PedidoCrudRepository pedidoRepository;
    private final DetallePedidoCrudRepository detalleRepository;
    private final ProductoCrudRepository productoRepository;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final AuthenticatedUserService authenticatedUserService;

    // Estados de pedido válidos
    private static final Set<String> VALID_STATUSES = Set.of("PENDIENTE", "CONFIRMADO", "ENVIADO", "COMPLETADO", "RECHAZADO");

    public OrderService(
            PedidoCrudRepository pedidoRepository,
            DetallePedidoCrudRepository detalleRepository,
            ProductoCrudRepository productoRepository,
            OrderMapper orderMapper,
            UserMapper userMapper,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.pedidoRepository = pedidoRepository;
        this.detalleRepository = detalleRepository;
        this.productoRepository = productoRepository;
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
            // No es necesario guardar aquí, la transacción lo hará al final.

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

    // --- NUEVO MÉTODO PARA RF11 ---
    public List<Order> getAllOrders() {
        return orderMapper.toOrders(pedidoRepository.findAll());
    }

    // --- NUEVO MÉTODO PARA RF11 ---
    @Transactional
    public Order updateOrderStatus(Long orderId, String newStatus) {
        // 1. Validar que el estado sea uno de los permitidos
        if (!VALID_STATUSES.contains(newStatus.toUpperCase())) {
            throw new IllegalArgumentException("Estado de pedido no válido: " + newStatus);
        }

        // 2. Encontrar el pedido
        Pedido pedido = pedidoRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + orderId));

        String oldStatus = pedido.getEstado();

        // 3. Lógica de negocio para RECHAZO (devolver stock)
        if ("RECHAZADO".equalsIgnoreCase(newStatus) && !"RECHAZADO".equalsIgnoreCase(oldStatus)) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                Producto producto = detalle.getProducto();
                producto.setStockDisponible(producto.getStockDisponible() + detalle.getCantidad());
                // No es necesario guardar producto por producto, la transacción se encarga
            }
        }

        // Aquí podrías añadir lógica para otros estados, ej: enviar email al confirmar.

        // 4. Actualizar el estado y guardar
        pedido.setEstado(newStatus.toUpperCase());
        Pedido updatedPedido = pedidoRepository.save(pedido);

        return orderMapper.toOrder(updatedPedido);
    }
}