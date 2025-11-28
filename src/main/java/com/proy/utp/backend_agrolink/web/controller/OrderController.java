package com.proy.utp.backend_agrolink.web.controller;

import com.proy.utp.backend_agrolink.domain.Order;
import com.proy.utp.backend_agrolink.domain.dto.OrderRequest;
import com.proy.utp.backend_agrolink.domain.dto.UpdateOrderStatusRequest; // <-- IMPORTAR
import com.proy.utp.backend_agrolink.domain.service.OrderService;
import org.springframework.http.HttpStatus; // <-- IMPORTAR
import org.springframework.http.ResponseEntity; // <-- IMPORTAR
import org.springframework.security.access.prepost.PreAuthorize; // <-- IMPORTAR
import org.springframework.web.bind.annotation.*;

import java.util.List; // <-- IMPORTAR

@RestController
@RequestMapping("/pedidos")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasRole('COMPRADOR')") // Solo los compradores pueden crear pedidos
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request) {
        try {
            Order newOrder = orderService.createOrder(request);
            return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Manejar errores como producto no encontrado o stock insuficiente
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('COMPRADOR') or hasRole('ADMINISTRADOR')") // Comprador (si es suyo) o Admin
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        // Aquí faltaría una validación para que el comprador solo vea sus propios pedidos.
        // Por ahora, lo dejamos así para que el admin pueda verlo.
        try {
            return new ResponseEntity<>(orderService.getOrderById(id), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // --- NUEVO ENDPOINT PARA RF11: Listar todos los pedidos ---
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // --- NUEVO ENDPOINT PARA RF11: Actualizar estado de un pedido ---
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody UpdateOrderStatusRequest request) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(id, request.getStatus());
            return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Si el estado no es válido
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Si el pedido no se encuentra
        }
    }
}