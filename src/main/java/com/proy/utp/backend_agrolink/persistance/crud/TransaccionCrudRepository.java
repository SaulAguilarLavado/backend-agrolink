package com.proy.utp.backend_agrolink.persistance.crud;

import com.proy.utp.backend_agrolink.persistance.entity.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDateTime;

public interface TransaccionCrudRepository extends JpaRepository<Transaccion, Long> {
    List<Transaccion> findByVendedorId(Long vendedorId);
    List<Transaccion> findByCompradorId(Long compradorId);
    List<Transaccion> findByFechaTransaccionBetween(LocalDateTime start, LocalDateTime end);

    // 🔥 AGREGAR ESTE MÉTODO
    List<Transaccion> findByPedidoId(Long pedidoId);
}