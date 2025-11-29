package com.proy.utp.backend_agrolink.web.controller;

import com.proy.utp.backend_agrolink.domain.Product;
import com.proy.utp.backend_agrolink.domain.dto.ProductUpdateRequest;
import com.proy.utp.backend_agrolink.domain.dto.StockAdjustmentRequest;
import com.proy.utp.backend_agrolink.domain.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Endpoint para que todos los roles vean la lista completa de productos
    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return new ResponseEntity<>(productService.getAll(), HttpStatus.OK);
    }

    // Endpoint para filtrar productos
    @GetMapping("/filtrar")
    public List<Product> filterProducts(
            @RequestParam(required=false) String nombre,
            @RequestParam(required=false) String unidad,
            @RequestParam(required=false) Double maxPrecio,
            @RequestParam(required=false) Double minCantidad
    ) {
        return productService.filterProducts(nombre, unidad, maxPrecio, minCantidad);
    }

    // Endpoint para que un agricultor vea su propio inventario
    @GetMapping("/mi-inventario")
    @PreAuthorize("hasRole('AGRICULTOR')")
    public ResponseEntity<List<Product>> getMyInventory(Authentication authentication) {
        String farmerEmail = authentication.getName();
        return ResponseEntity.ok(productService.getProductsByFarmer(farmerEmail));
    }

    // Endpoint para ver un producto específico por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable("id") long productId) {
        return productService.getProduct(productId)
                .map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Endpoint para que un agricultor cree un nuevo producto
    @PostMapping
    @PreAuthorize("hasRole('AGRICULTOR')")
    public ResponseEntity<Product> save(@RequestBody Product product, Authentication authentication) {
        String farmerEmail = authentication.getName();
        Product savedProduct = productService.saveForFarmer(product, farmerEmail);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    // Endpoint para que un agricultor actualice un producto existente
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AGRICULTOR')")
    public ResponseEntity<Product> update(
            @PathVariable("id") Long productId,
            @RequestBody ProductUpdateRequest request,
            Authentication authentication) {

        String farmerEmail = authentication.getName();
        try {
            return productService.updateProduct(productId, request, farmerEmail)
                    .map(ResponseEntity::ok)
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // Endpoint para que un agricultor elimine un producto
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AGRICULTOR')")
    public ResponseEntity<Void> delete(@PathVariable("id") long productId) {
        if (productService.delete(productId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // --- ENDPOINT PARA AJUSTAR STOCK (EL QUE FALTABA) ---
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('AGRICULTOR')")
    public ResponseEntity<Product> adjustStock(
            @PathVariable("id") Long productId,
            @RequestBody StockAdjustmentRequest request,
            Authentication authentication) {

        String farmerEmail = authentication.getName();
        try {
            return productService.adjustStock(productId, request.getDelta(), farmerEmail)
                    .map(ResponseEntity::ok)
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            // Este error se lanza si se intenta dejar el stock en negativo
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}