package com.proy.utp.backend_agrolink.persistance.crud;

import com.proy.utp.backend_agrolink.persistance.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoCrudRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByCompradorId(Long compradorId);
}