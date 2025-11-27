package com.proy.utp.backend_agrolink.web.controller;

import com.proy.utp.backend_agrolink.domain.Product;
import com.proy.utp.backend_agrolink.domain.dto.ProductUpdateRequest; // <-- Nuevo import
import com.proy.utp.backend_agrolink.domain.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // <-- Importante
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Este endpoint permite a todos los roles ver la lista completa de productos
    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return new ResponseEntity<>(productService.getAll(), HttpStatus.OK);
    }

    // --- NUEVO ENDPOINT ---
    /**
     * Endpoint para que un agricultor vea su propio inventario.
     */
    @GetMapping("/mi-inventario")
    @PreAuthorize("hasRole('AGRICULTOR')")
    public ResponseEntity<List<Product>> getMyInventory(Authentication authentication) {
        String farmerEmail = authentication.getName();
        return ResponseEntity.ok(productService.getProductsByFarmer(farmerEmail));
    }

    // Este endpoint permite a cualquier rol autenticado ver un producto específico
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable("id") long productId) {
        return productService.getProduct(productId)
                .map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Se mantiene igual, pero añadimos seguridad
    @PostMapping
    @PreAuthorize("hasRole('AGRICULTOR')")
    public ResponseEntity<Product> save(@RequestBody Product product, Authentication authentication) {
        String farmerEmail = authentication.getName();
        Product savedProduct = productService.saveForFarmer(product, farmerEmail);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    // --- NUEVO ENDPOINT PARA ACTUALIZAR ---
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

    // Se mantiene igual, pero añadimos seguridad
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AGRICULTOR')")
    public ResponseEntity<Void> delete(@PathVariable("id") long productId) {
        // Es una buena práctica validar que el producto pertenece al agricultor antes de borrar,
        // esto se puede añadir en el service.
        if (productService.delete(productId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}