package com.proy.utp.backend_agrolink.persistance.mapper;

import com.proy.utp.backend_agrolink.domain.Order;
import com.proy.utp.backend_agrolink.persistance.entity.Pedido;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, OrderDetailMapper.class})
public interface OrderMapper {

    @Mappings({
            @Mapping(source = "id", target = "orderId"),
            @Mapping(source = "fechaPedido", target = "orderDate"),
            @Mapping(source = "estado", target = "status"),
            @Mapping(source = "total", target = "totalAmount"),
            @Mapping(source = "comprador", target = "buyer"),
            @Mapping(source = "detalles", target = "details")
    })
    Order toOrder(Pedido pedido);

    List<Order> toOrders(List<Pedido> pedidos);

    @InheritInverseConfiguration
    @Mapping(target = "detalles", ignore = true) // Evitamos bucle, se setea en service
    Pedido toPedido(Order order);
}
