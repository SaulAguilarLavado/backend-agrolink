package com.proy.utp.backend_agrolink.web.controller;

import com.proy.utp.backend_agrolink.domain.Harvest;
import com.proy.utp.backend_agrolink.domain.dto.HarvestRegistrationRequest;
import com.proy.utp.backend_agrolink.domain.service.HarvestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cosechas")
public class HarvestController {

    @Autowired
    private HarvestService harvestService;

    @GetMapping("/my-harvests")
    @PreAuthorize("hasRole('AGRICULTOR')")
    public ResponseEntity<List<Harvest>> getMyHarvests(Authentication authentication) {
        String farmerEmail = authentication.getName();
        try {
            List<Harvest> list = harvestService.findByFarmerEmail(farmerEmail);
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (RuntimeException e) {
            // ajustar manejo de errores según convenga
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('AGRICULTOR')") // Solo los agricultores pueden registrar cosechas
    public ResponseEntity<Harvest> registerHarvest(
            @RequestBody HarvestRegistrationRequest request,
            Authentication authentication) {

        // Obtenemos el email del agricultor autenticado desde el token JWT
        String farmerEmail = authentication.getName();

        try {
            Harvest savedHarvest = harvestService.registerHarvest(request, farmerEmail);
            return new ResponseEntity<>(savedHarvest, HttpStatus.CREATED);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // Si el agricultor no es el dueño
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Si el cultivo no existe
        }
    }

    // Obtener una cosecha por id
    @GetMapping("/{id:[0-9]+}")
    @PreAuthorize("hasRole('AGRICULTOR') or hasRole('ADMIN')")
    public ResponseEntity<Harvest> getHarvest(@PathVariable Long id, Authentication authentication) {
        return harvestService.findByFarmerEmail(authentication.getName()).stream()
                .filter(h -> h.getId().equals(id))
                .findFirst()
                .map(h -> new ResponseEntity<>(h, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Actualizar cosecha
    @PutMapping("/{id:[0-9]+}")
    @PreAuthorize("hasRole('AGRICULTOR')")
    public ResponseEntity<Harvest> updateHarvest(@PathVariable Long id,
                                                 @RequestBody HarvestRegistrationRequest request,
                                                 Authentication authentication) {
        try {
            Harvest updated = harvestService.updateHarvest(id, request, authentication.getName());
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (SecurityException se) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (RuntimeException re) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Eliminar cosecha
    @DeleteMapping("/{id:[0-9]+}")
    @PreAuthorize("hasRole('AGRICULTOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteHarvest(@PathVariable Long id, Authentication authentication) {
        try {
            harvestService.deleteHarvest(id, authentication.getName());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (SecurityException se) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (RuntimeException re) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}