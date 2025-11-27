package com.proy.utp.backend_agrolink.persistance.mapper;

import com.proy.utp.backend_agrolink.domain.Harvest;
import com.proy.utp.backend_agrolink.persistance.entity.Cosecha;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HarvestMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "fechaCosecha", target = "harvestDate"),
            @Mapping(source = "cantidadRecogida", target = "quantityHarvested"),
            @Mapping(source = "unidadMedida", target = "unitOfMeasure"),
            @Mapping(source = "notas", target = "qualityNotes"),
            @Mapping(source = "cultivo.id", target = "cropId")
    })
    Harvest toHarvest(Cosecha cosecha);
    List<Harvest> toHarvests(List<Cosecha> cosechas);

    @InheritInverseConfiguration
    @Mappings({
            @Mapping(target = "cultivo", ignore = true),
            // --- ¡AQUÍ ESTÁ LA CORRECCIÓN! ---
            // Hemos eliminado la línea que hacía referencia a "productos"
    })
    Cosecha toCosecha(Harvest harvest);
}