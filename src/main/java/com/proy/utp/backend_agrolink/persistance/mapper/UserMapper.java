package com.proy.utp.backend_agrolink.persistance.mapper;

import com.proy.utp.backend_agrolink.domain.User;
import com.proy.utp.backend_agrolink.persistance.entity.Usuario;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {

    @Mappings({
            @Mapping(source = "id", target = "userId"),
            @Mapping(source = "nombre", target = "name"),
            @Mapping(source = "apellido", target = "lastname"),
            @Mapping(source = "direccion", target = "address"),
            @Mapping(source = "telefono", target = "phone")
    })

    User toUser(Usuario usuario);

    @InheritInverseConfiguration
    Usuario toUsuario(User user);
}
