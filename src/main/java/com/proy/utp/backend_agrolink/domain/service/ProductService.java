package com.proy.utp.backend_agrolink.domain.service;

import com.proy.utp.backend_agrolink.domain.Product;
import com.proy.utp.backend_agrolink.domain.User;
import com.proy.utp.backend_agrolink.domain.dto.ProductUpdateRequest; // <-- Nuevo import
import com.proy.utp.backend_agrolink.domain.repository.ProductRepository;
import com.proy.utp.backend_agrolink.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Product> getAll() {
        return productRepository.getAll();
    }

    // --- NUEVO MÉTODO ---
    /**
     * Obtiene todos los productos que pertenecen a un agricultor específico.
     * @param farmerEmail El email del agricultor autenticado.
     * @return Una lista de sus productos.
     */
    public List<Product> getProductsByFarmer(String farmerEmail) {
        User farmer = userRepository.findByEmail(farmerEmail)
                .orElseThrow(() -> new RuntimeException("Agricultor no encontrado"));
        return productRepository.findByFarmerId(farmer.getUserId());
    }

    public Optional<Product> getProduct(long productId) {
        return productRepository.findById(productId);
    }

    public Product saveForFarmer(Product product, String farmerEmail) {
        User farmer = userRepository.findByEmail(farmerEmail)
                .orElseThrow(() -> new RuntimeException("Agricultor no encontrado con email: " + farmerEmail));
        product.setFarmer(farmer);
        return productRepository.save(product);
    }

    // --- NUEVO MÉTODO PARA ACTUALIZAR ---
    /**
     * Actualiza un producto existente, validando que pertenezca al agricultor.
     * @param productId El ID del producto a actualizar.
     * @param request Los nuevos datos para el producto.
     * @param farmerEmail El email del agricultor autenticado.
     * @return El producto actualizado.
     */
    public Optional<Product> updateProduct(Long productId, ProductUpdateRequest request, String farmerEmail) {
        return productRepository.findById(productId)
                .map(product -> {
                    // Validación de seguridad: el producto debe pertenecer al agricultor
                    if (!product.getFarmer().getEmail().equals(farmerEmail)) {
                        throw new SecurityException("No tienes permiso para modificar este producto.");
                    }

                    // Actualizamos los campos
                    product.setName(request.getName());
                    product.setDescription(request.getDescription());
                    product.setPricePerUnit(request.getPricePerUnit());
                    product.setAvailableStock(request.getAvailableStock());

                    return productRepository.save(product);
                });
    }

    public boolean delete(long productId) {
        // Podríamos añadir una validación de seguridad aquí también antes de borrar
        return getProduct(productId).map(product -> {
            productRepository.deleteById(productId);
            return true;
        }).orElse(false);
    }
}