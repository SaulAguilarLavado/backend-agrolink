package com.proy.utp.backend_agrolink.domain.repository;

import com.proy.utp.backend_agrolink.domain.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    List<Product> getAll();

    // Unificamos el nombre a findById, que es el estándar.
    Optional<Product> findById(long productId);

    // Este método es nuevo y lo necesitarás, así que lo añadimos.
    List<Product> findByFarmerId(long farmerId);

    // Buscar productos por nombre de cultivo asociado (vía cosecha -> cultivo.nombre)
    List<Product> findByCropNameLike(String cropName);

    Product save(Product product);

    // Unificamos el nombre a deleteById, que es más explícito y estándar.
    void deleteById(long productId);
}