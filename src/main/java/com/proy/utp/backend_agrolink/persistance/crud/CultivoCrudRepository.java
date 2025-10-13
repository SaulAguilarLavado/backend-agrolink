package com.proy.utp.backend_agrolink.persistance.crud;

import com.proy.utp.backend_agrolink.persistance.entity.Cultivo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CultivoCrudRepository extends CrudRepository<Cultivo, Long> {
    List<Cultivo> findByAgricultorIdOrderByFechaSiembraDesc(Long agricultorId);

}
