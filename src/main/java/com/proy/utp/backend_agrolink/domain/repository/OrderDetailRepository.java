package com.proy.utp.backend_agrolink.domain.repository;

import com.proy.utp.backend_agrolink.domain.OrderDetail;
import java.util.List;

public interface OrderDetailRepository {

    OrderDetail save(OrderDetail detail);

    List<OrderDetail> getByOrder(Long orderId);
}
