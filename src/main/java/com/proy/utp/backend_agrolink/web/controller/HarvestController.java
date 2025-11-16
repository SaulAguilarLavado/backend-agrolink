package com.proy.utp.backend_agrolink.web.controller;

import com.proy.utp.backend_agrolink.domain.Harvest;
import com.proy.utp.backend_agrolink.domain.dto.HarvestRegistrationRequest;
import com.proy.utp.backend_agrolink.domain.service.HarvestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cosechas")
public class HarvestController {

    @Autowired
    private HarvestService harvestService;

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
}