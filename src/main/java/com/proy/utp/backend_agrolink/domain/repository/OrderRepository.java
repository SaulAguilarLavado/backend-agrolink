package com.proy.utp.backend_agrolink.domain.repository;

import com.proy.utp.backend_agrolink.domain.Order;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> getById(Long orderId);

    List<Order> getOrdersByUser(Long userId);

    List<Order> getAll();
}
