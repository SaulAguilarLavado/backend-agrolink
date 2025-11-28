package com.proy.utp.backend_agrolink.persistance.mapper;

import com.proy.utp.backend_agrolink.domain.OrderDetail;
import com.proy.utp.backend_agrolink.persistance.entity.DetallePedido;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {

    // ENTITY → DOMAIN
    @Mapping(source = "id",              target = "detailId")
    @Mapping(source = "cantidad",        target = "quantity")
    @Mapping(source = "precioUnitario",  target = "unitPrice")
    @Mapping(source = "producto.id",     target = "productId")
    @Mapping(source = "producto.nombre", target = "productName")
    OrderDetail toOrderDetail(DetallePedido detalle);

    List<OrderDetail> toOrderDetails(List<DetallePedido> detalles);


    // DOMAIN → ENTITY
    @InheritInverseConfiguration
    @Mapping(target = "producto", ignore = true)  // Se asigna en OrderService
    @Mapping(target = "pedido",   ignore = true)  // Se asigna en OrderService
    DetallePedido toDetallePedido(OrderDetail detail);
}
