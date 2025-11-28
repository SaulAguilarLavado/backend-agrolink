package com.proy.utp.backend_agrolink.persistance;

import com.proy.utp.backend_agrolink.domain.OrderDetail;
import com.proy.utp.backend_agrolink.domain.repository.OrderDetailRepository;
import com.proy.utp.backend_agrolink.persistance.crud.DetallePedidoCrudRepository;
import com.proy.utp.backend_agrolink.persistance.mapper.OrderDetailMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DetallePedidoRepository implements OrderDetailRepository {

    private final DetallePedidoCrudRepository crud;
    private final OrderDetailMapper mapper;

    public DetallePedidoRepository(DetallePedidoCrudRepository crud, OrderDetailMapper mapper) {
        this.crud = crud;
        this.mapper = mapper;
    }

    @Override
    public OrderDetail save(OrderDetail detail) {
        return mapper.toOrderDetail(
                crud.save(mapper.toDetallePedido(detail))
        );
    }

    @Override
    public List<OrderDetail> getByOrder(Long orderId) {
        return mapper.toOrderDetails(crud.findByPedidoId(orderId));
    }
}
