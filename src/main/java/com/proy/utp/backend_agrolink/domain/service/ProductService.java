package com.proy.utp.backend_agrolink.domain.service;

import com.proy.utp.backend_agrolink.domain.Product;
import com.proy.utp.backend_agrolink.domain.repository.ProductRepository;
import com.proy.utp.backend_agrolink.persistance.crud.ProductoCrudRepository;
import com.proy.utp.backend_agrolink.persistance.crud.UsuarioCrudRepository; // <-- 1. IMPORTAMOS EL CRUD REPOSITORY
import com.proy.utp.backend_agrolink.persistance.entity.Usuario; // <-- 2. IMPORTAMOS LA ENTIDAD
import com.proy.utp.backend_agrolink.persistance.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.proy.utp.backend_agrolink.persistance.entity.Producto; // <-- 2. IMPORTAMOS LA ENTIDAD

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UsuarioCrudRepository usuarioCrudRepository; // <-- 3. INYECTAMOS EL CRUD REPOSITORY

    @Autowired
    private ProductMapper productMapper; // <-- 4. INYECTAMOS EL MAPPER
    @Autowired
    private ProductoCrudRepository productoCrudRepository;

    public List<Product> getAll() {
        return productRepository.getAll();
    }

    public Optional<Product> getProduct(long productId) {
        return productRepository.findById(productId);
    }

    /**
     * Guarda un nuevo producto, asignando automáticamente
     * al agricultor que está actualmente logueado.
     */
    public Product save(Product product) {
        // 1. Obtener el email del usuario logueado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        // 2. Buscar la ENTIDAD 'Usuario' del agricultor
        Usuario farmerEntity = usuarioCrudRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario agricultor no encontrado"));

        // 3. Convertir el 'Product' del dominio a una entidad 'Producto'
        Producto productoEntity = productMapper.toProducto(product);

        // 4. Asignar el agricultor a la entidad del producto
        productoEntity.setAgricultor(farmerEntity);

        // 5. Guardar la entidad 'Producto' usando el CRUD REPOSITORY DIRECTAMENTE
        Producto savedProductoEntity = productoCrudRepository.save(productoEntity);

        // 6. Convertir la entidad guardada de vuelta a un 'Product' de dominio para devolverla
        return productMapper.toProduct(savedProductoEntity);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or @productRepository.findById(#productId).get().getFarmer().getEmail() == authentication.name")
    public boolean delete(long productId) {
        return getProduct(productId).map(product -> {
            productRepository.deleteById(productId);
            return true;
        }).orElse(false);
    }
}