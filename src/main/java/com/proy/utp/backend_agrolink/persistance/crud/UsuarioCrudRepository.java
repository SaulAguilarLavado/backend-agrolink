package com.proy.utp.backend_agrolink.persistance.crud;

import com.proy.utp.backend_agrolink.persistance.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioCrudRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Boolean existsByEmail(String email);
    List<Usuario> findByRoles_Nombre(String nombre);
}
