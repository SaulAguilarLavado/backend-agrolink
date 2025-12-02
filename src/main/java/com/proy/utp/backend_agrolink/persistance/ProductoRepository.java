package com.proy.utp.backend_agrolink.persistance;

import com.proy.utp.backend_agrolink.domain.Product;
import com.proy.utp.backend_agrolink.domain.repository.ProductRepository;
import com.proy.utp.backend_agrolink.persistance.crud.ProductoCrudRepository;
import com.proy.utp.backend_agrolink.persistance.entity.Producto;
import com.proy.utp.backend_agrolink.persistance.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductoRepository implements ProductRepository {

    @Autowired
    private ProductoCrudRepository productoCrudRepository;

    @Autowired
    private ProductMapper mapper;

    @Override
    public List<Product> getAll() {
        return mapper.toProducts(productoCrudRepository.findAll());
    }

    @Override
    public Optional<Product> findById(long productId) {
        return productoCrudRepository.findById(productId).map(mapper::toProduct);
    }

    @Override
    public List<Product> findByFarmerId(long farmerId) {
        return mapper.toProducts(productoCrudRepository.findByAgricultorId(farmerId));
    }

    @Override
    public List<Product> findByCropNameLike(String cropName) {
        return mapper.toProducts(productoCrudRepository.findByCosechaCultivoNombreContainingIgnoreCase(cropName));
    }

    @Override
    public Product save(Product product) {
        Producto productoEntity = mapper.toProducto(product);
        return mapper.toProduct(productoCrudRepository.save(productoEntity));
    }

    @Override
    public void deleteById(long productId) {
        productoCrudRepository.deleteById(productId);
    }
}