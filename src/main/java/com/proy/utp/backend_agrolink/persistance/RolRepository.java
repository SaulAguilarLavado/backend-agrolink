package com.proy.utp.backend_agrolink.persistance;

import com.proy.utp.backend_agrolink.domain.Role;
import com.proy.utp.backend_agrolink.domain.repository.RoleRepository;
import com.proy.utp.backend_agrolink.persistance.crud.RolCrudRepository;
import com.proy.utp.backend_agrolink.persistance.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RolRepository implements RoleRepository {

    @Autowired
    private RolCrudRepository rolCrudRepository;

    @Autowired
    private RoleMapper mapper;

    @Override
    public Optional<Role> findByRoleName(String roleName) {
        return rolCrudRepository.findByNombre(roleName)
                .map(rol -> mapper.toRole(rol));
    }
}