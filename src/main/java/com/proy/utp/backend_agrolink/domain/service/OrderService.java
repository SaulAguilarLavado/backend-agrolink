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
import com.proy.utp.backend_agrolink.persistance.mapper.OrderDetailMapper;
import com.proy.utp.backend_agrolink.persistance.mapper.ProductMapper;
import com.proy.utp.backend_agrolink.persistance.mapper.UserMapper;
import com.proy.utp.backend_agrolink.domain.service.AuthenticatedUserService;

import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final PedidoCrudRepository pedidoRepository;
    private final DetallePedidoCrudRepository detalleRepository;
    private final ProductoCrudRepository productoRepository;

    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;   //  Necesario para convertir Domain → Entity

    private final AuthenticatedUserService authenticatedUserService; //  Servicio correcto

    public OrderService(
            PedidoCrudRepository pedidoRepository,
            DetallePedidoCrudRepository detalleRepository,
            ProductoCrudRepository productoRepository,
            OrderMapper orderMapper,
            OrderDetailMapper orderDetailMapper,
            ProductMapper productMapper,
            UserMapper userMapper,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.pedidoRepository = pedidoRepository;
        this.detalleRepository = detalleRepository;
        this.productoRepository = productoRepository;
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.productMapper = productMapper;
        this.userMapper = userMapper;
        this.authenticatedUserService = authenticatedUserService;
    }

    public Order createOrder(OrderRequest request) {

        // 1️ Obtener usuario autenticado
        var buyerDomain = authenticatedUserService.getAuthenticatedUser();
        if (buyerDomain == null)
            throw new RuntimeException("Usuario no autenticado");

        // Convertir dominio → entidad
        Usuario buyerEntity = userMapper.toUsuario(buyerDomain);

        // 2️ Crear Pedido entity
        Pedido pedido = new Pedido();
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado("PENDIENTE");
        pedido.setComprador(buyerEntity);
        pedido.setTotal(BigDecimal.ZERO);

        List<DetallePedido> detalleEntities = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        // 3️ Procesar cada item
        for (OrderItem item : request.getItems()) {

            Producto producto = productoRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getProductId()));

            if (producto.getStockDisponible() < item.getQuantity()) {
                throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
            }

            // Descontar stock
            producto.setStockDisponible(producto.getStockDisponible() - item.getQuantity());
            productoRepository.save(producto);

            // Crear detalle
            DetallePedido det = new DetallePedido();
            det.setProducto(producto);
            det.setCantidad(item.getQuantity());
            det.setPrecioUnitario(producto.getPrecioPorUnidad());
            det.setPedido(pedido);

            detalleEntities.add(det);

            total = total.add(
                    producto.getPrecioPorUnidad().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }

        pedido.setTotal(total);
        pedido.setDetalles(detalleEntities);

        // 4️ Guardar pedido
        Pedido saved = pedidoRepository.save(pedido);

        return orderMapper.toOrder(saved);
    }
}
