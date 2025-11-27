package com.proy.utp.backend_agrolink.persistance.crud;

import com.proy.utp.backend_agrolink.persistance.entity.Cosecha;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CosechaCrudRepository extends JpaRepository<Cosecha, Long> {
    // Metodo útil para el futuro: encontrar todas las cosechas de un cultivo específico
    List<Cosecha> findByCultivoId(Long cultivoId);
    
    // Encuentra todas las cosechas cuyo cultivo pertenece a un agricultor (ordenadas por fecha de cosecha descendente)
    List<Cosecha> findByCultivoAgricultorIdOrderByFechaCosechaDesc(Long agricultorId);
}