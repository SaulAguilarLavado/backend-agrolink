package com.proy.utp.backend_agrolink.domain.service;

import com.proy.utp.backend_agrolink.domain.Product;
import com.proy.utp.backend_agrolink.domain.User;
import com.proy.utp.backend_agrolink.domain.repository.ProductRepository;
import com.proy.utp.backend_agrolink.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    // Un servicio del dominio solo debe depender de repositorios del dominio.
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

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
    public Product saveForFarmer(Product product, String farmerEmail) {
        User farmer = userRepository.findByEmail(farmerEmail)
                .orElseThrow(() -> new RuntimeException("Agricultor no encontrado con email: " + farmerEmail));

        product.setFarmer(farmer);

        return productRepository.save(product);
    }

    /**
     * Borra un producto si existe.
     * La lógica de permisos (si el usuario puede borrarlo) ya está en SecurityConfig.
     */
    public boolean delete(long productId) {
        return getProduct(productId).map(product -> {
            productRepository.deleteById(productId);
            return true;
        }).orElse(false);
    }
}