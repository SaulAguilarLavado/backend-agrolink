package com.proy.utp.backend_agrolink.persistance.mapper;

import com.proy.utp.backend_agrolink.domain.Role;
import com.proy.utp.backend_agrolink.persistance.entity.Rol;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mappings({
            @Mapping(source = "id", target = "roleId"),
            @Mapping(source = "nombre", target = "roleName")
    })
    Role toRole(Rol rol);

    @InheritInverseConfiguration
    Rol toRol(Role role);
}
