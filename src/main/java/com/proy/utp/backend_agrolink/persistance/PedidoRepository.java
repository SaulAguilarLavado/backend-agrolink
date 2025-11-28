package com.proy.utp.backend_agrolink.persistance;

import com.proy.utp.backend_agrolink.domain.Order;
import com.proy.utp.backend_agrolink.domain.repository.OrderRepository;
import com.proy.utp.backend_agrolink.persistance.crud.PedidoCrudRepository;
import com.proy.utp.backend_agrolink.persistance.entity.Pedido;
import com.proy.utp.backend_agrolink.persistance.mapper.OrderMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PedidoRepository implements OrderRepository {

    private final PedidoCrudRepository crud;
    private final OrderMapper mapper;

    public PedidoRepository(PedidoCrudRepository crud, OrderMapper mapper) {
        this.crud = crud;
        this.mapper = mapper;
    }

    @Override
    public Order save(Order order) {
        Pedido entity = mapper.toPedido(order);
        return mapper.toOrder(crud.save(entity));
    }

    @Override
    public Optional<Order> getById(Long id) {
        return crud.findById(id).map(mapper::toOrder);
    }

    @Override
    public List<Order> getOrdersByUser(Long userId) {
        return mapper.toOrders(crud.findByCompradorId(userId));
    }

    @Override
    public List<Order> getAll() {
        return mapper.toOrders((List<Pedido>) crud.findAll());
    }
}
