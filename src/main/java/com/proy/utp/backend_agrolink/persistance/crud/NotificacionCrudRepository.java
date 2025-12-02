package com.proy.utp.backend_agrolink.persistance.crud;

import com.proy.utp.backend_agrolink.persistance.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacionCrudRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
}
