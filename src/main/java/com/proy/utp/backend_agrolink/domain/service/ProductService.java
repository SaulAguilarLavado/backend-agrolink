package com.proy.utp.backend_agrolink.domain.service;

import com.proy.utp.backend_agrolink.domain.Product;
import com.proy.utp.backend_agrolink.domain.User;
import com.proy.utp.backend_agrolink.domain.dto.ProductUpdateRequest;
import com.proy.utp.backend_agrolink.domain.repository.ProductRepository;
import com.proy.utp.backend_agrolink.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if (product.getPricePerUnit() == null || product.getPricePerUnit().doubleValue() <= 0) {
            throw new IllegalArgumentException("El precio por unidad debe ser mayor a 0");
        }
        if (product.getAvailableStock() < 0) {
            throw new IllegalArgumentException("El stock disponible no puede ser negativo");
        }

        // --- ¡SOLUCIÓN AQUÍ! ---
        // Asignamos un estado por defecto antes de guardar.
        product.setState("DISPONIBLE");
        // -------------------------

        return productRepository.save(product);
    }

    public Optional<Product> updateProduct(Long productId, ProductUpdateRequest request, String farmerEmail) {
        return productRepository.findById(productId)
                .map(product -> {
                    if (!product.getFarmer().getEmail().equals(farmerEmail)) {
                        throw new SecurityException("No tienes permiso para modificar este producto.");
                    }
                    product.setName(request.getName());
                    product.setDescription(request.getDescription());
                    product.setPricePerUnit(request.getPricePerUnit());
                    product.setAvailableStock(request.getAvailableStock());
                    return productRepository.save(product);
                });
    }

    @Transactional // ¡Muy importante para operaciones de actualización!
    public Optional<Product> adjustStock(Long productId, Double delta, String farmerEmail) {
        return productRepository.findById(productId)
                .map(product -> {
                    // Validación de seguridad: el producto debe pertenecer al agricultor
                    if (!product.getFarmer().getEmail().equals(farmerEmail)) {
                        throw new SecurityException("No tienes permiso para modificar el stock de este producto.");
                    }

                    double newStock = product.getAvailableStock() + delta;

                    // Validación de negocio: no permitir stock negativo
                    if (newStock < 0) {
                        throw new IllegalArgumentException("El stock no puede ser negativo. Stock actual: " + product.getAvailableStock());
                    }

                    product.setAvailableStock(newStock);
                    return productRepository.save(product);
                });
    }

    public boolean delete(long productId) {
        return getProduct(productId).map(product -> {
            productRepository.deleteById(productId);
            return true;
        }).orElse(false);
    }

    public List<Product> filterProducts(String nombre, String unidad, Double maxPrecio, Double minCantidad, String tipoCultivo) {
        // Punto de partida: si se especifica tipoCultivo real (nombre de cultivo), consultamos por relación
        List<Product> all;
        if (tipoCultivo != null && !tipoCultivo.trim().isEmpty()) {
            all = productRepository.findByCropNameLike(tipoCultivo.trim());
        } else {
            all = getAll();
        }
        return all.stream()
                .filter(p -> nombre == null || p.getName().toLowerCase().contains(nombre.toLowerCase()))
                .filter(p -> unidad == null || p.getUnitOfMeasure().equalsIgnoreCase(unidad))
                .filter(p -> maxPrecio == null || p.getPricePerUnit().doubleValue() <= maxPrecio)
                .filter(p -> minCantidad == null || p.getAvailableStock() >= minCantidad)
                .toList();
    }
}