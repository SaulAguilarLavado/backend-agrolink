package com.proy.utp.backend_agrolink.web.controller;

import com.proy.utp.backend_agrolink.domain.Order;
import com.proy.utp.backend_agrolink.domain.dto.OrderRequest;
import com.proy.utp.backend_agrolink.domain.dto.UpdateOrderStatusRequest;
import com.proy.utp.backend_agrolink.domain.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasRole('COMPRADOR')")
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request) {
        try {
            Order newOrder = orderService.createOrder(request);
            return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('COMPRADOR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(orderService.getOrderById(id), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody UpdateOrderStatusRequest request) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(id, request.getStatus());
            return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // --- ENDPOINT PARA RF14: Historial de pedidos del comprador ---
    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('COMPRADOR')")
    public ResponseEntity<List<Order>> getMyOrders() {
        List<Order> orders = orderService.getOrdersForAuthenticatedUser();
        return ResponseEntity.ok(orders);
    }
}