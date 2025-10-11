package com.proy.utp.backend_agrolink.domain.repository;

import com.proy.utp.backend_agrolink.domain.Role;
import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findByRoleName(String roleName);
}