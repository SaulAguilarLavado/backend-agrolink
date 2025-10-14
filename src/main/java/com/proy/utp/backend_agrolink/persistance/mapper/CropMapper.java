package com.proy.utp.backend_agrolink.persistance.mapper;

import com.proy.utp.backend_agrolink.domain.Crop; // <-- Objeto de dominio
import com.proy.utp.backend_agrolink.persistance.entity.Cultivo;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class}) // Es clave que use el UserMapper
public interface CropMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "nombre", target = "name"),
            @Mapping(source = "descripcion", target = "description"),
            @Mapping(source = "fechaSiembra", target = "plantingDate"),
            @Mapping(source = "areaCultivada", target = "cultivatedArea"),
            @Mapping(source = "agricultor", target = "farmer") // Mapeo de objeto a objeto
    })
    Crop toCrop(Cultivo cultivo);
    List<Crop> toCrops(List<Cultivo> cultivos);

    @InheritInverseConfiguration
    Cultivo toCultivo(Crop crop);
}