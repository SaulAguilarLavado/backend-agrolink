package com.proy.utp.backend_agrolink.persistance.mapper;

import com.proy.utp.backend_agrolink.domain.dto.CropDTO;
import com.proy.utp.backend_agrolink.persistance.entity.Cultivo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CropMapper {

    @Mappings({
            @Mapping(source = "nombre", target = "name"),
            @Mapping(source = "descripcion", target = "description"),
            @Mapping(source = "fechaSiembra", target = "plantingDate"),
            @Mapping(source = "areaCultivada", target = "cultivatedArea"),
            @Mapping(source = "agricultor.id", target = "farmerId")
    })
    CropDTO toCropDTO(Cultivo cultivo);

    List<CropDTO>toCrops(List<Cultivo> cultivos);

    @Mappings({
            @Mapping(source = "name", target = "nombre"),
            @Mapping(source = "description", target = "descripcion"),
            @Mapping(source = "plantingDate", target = "fechaSiembra"),
            @Mapping(source = "cultivatedArea", target = "areaCultivada"),
            @Mapping(source = "farmerId", target = "agricultor.id")
    })
    Cultivo toCultivo(CropDTO cropDTO);
}
