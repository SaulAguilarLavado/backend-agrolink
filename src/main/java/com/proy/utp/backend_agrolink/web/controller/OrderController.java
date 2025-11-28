package com.proy.utp.backend_agrolink.web.controller;

import com.proy.utp.backend_agrolink.domain.Order;
import com.proy.utp.backend_agrolink.domain.dto.OrderRequest;
import com.proy.utp.backend_agrolink.domain.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Order createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }
    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }
}
