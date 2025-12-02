package com.proy.utp.backend_agrolink.persistance.crud;

import com.proy.utp.backend_agrolink.persistance.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoCrudRepository extends JpaRepository<Producto, Long> {
    // Spring Data crea automáticamente la consulta a partir del nombre del método
    List<Producto> findByAgricultorId(Long agricultorId);

    // Buscar por nombre de cultivo asociado (cosecha -> cultivo -> nombre)
    List<Producto> findByCosechaCultivoNombreContainingIgnoreCase(String nombre);
}