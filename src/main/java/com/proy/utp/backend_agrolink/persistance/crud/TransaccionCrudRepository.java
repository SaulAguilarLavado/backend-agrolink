package com.proy.utp.backend_agrolink.persistance.crud;

import com.proy.utp.backend_agrolink.persistance.entity.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransaccionCrudRepository extends JpaRepository<Transaccion, Long> {
    // Métodos para futuros reportes (RF13)
    List<Transaccion> findByVendedorId(Long vendedorId);
    List<Transaccion> findByCompradorId(Long compradorId);
}