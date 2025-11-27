package com.proy.utp.backend_agrolink.persistance.mapper;

import com.proy.utp.backend_agrolink.domain.Product;
import com.proy.utp.backend_agrolink.persistance.entity.Producto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class}) // Reutilizamos el UserMapper
public interface ProductMapper {

    @Mappings({
            @Mapping(source = "id", target = "productId"),
            @Mapping(source = "nombre", target = "name"),
            @Mapping(source = "descripcion", target = "description"),
            @Mapping(source = "precioPorUnidad", target = "pricePerUnit"),
            @Mapping(source = "unidadMedida", target = "unitOfMeasure"),
            @Mapping(source = "stockDisponible", target = "availableStock"),
            @Mapping(source = "agricultor", target = "farmer"),
            @Mapping(source = "cosecha.id", target = "harvestId"),
            @Mapping(source = "estado", target = "state"),
            @Mapping(source = "fechaPublicacion", target = "publishDate")// Mapeamos solo el ID de la cosecha
    })
    Product toProduct(Producto producto);

    List<Product> toProducts(List<Producto> productos);

    @InheritInverseConfiguration
    @Mapping(target = "cosecha", ignore = true) // Ignoramos la cosecha al mapear de vuelta
    Producto toProducto(Product product);
}