package com.proy.utp.backend_agrolink.web.controller;

import com.proy.utp.backend_agrolink.domain.Crop;
import com.proy.utp.backend_agrolink.domain.service.CropService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cultivos")
public class CropController {

    @Autowired
    private CropService cropService;

    // Endpoint seguro para que un agricultor vea SUS PROPIOS cultivos
    @GetMapping("/my-crops")
    public ResponseEntity<List<Crop>> getMyCrops(Authentication authentication) {
        String farmerEmail = authentication.getName();
        return new ResponseEntity<>(cropService.findByAuthenticatedFarmer(farmerEmail), HttpStatus.OK);
    }

    // Obtener un cultivo por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Crop> getCrop(@PathVariable Long id) {
        return cropService.findById(id)
                .map(crop -> new ResponseEntity<>(crop, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Guardar un nuevo cultivo. El agricultor se asigna desde el token.
    @PostMapping
    public ResponseEntity<Crop> save(@RequestBody Crop crop, Authentication authentication) {
        String farmerEmail = authentication.getName();
        Crop savedCrop = cropService.saveForAuthenticatedFarmer(crop, farmerEmail);
        return new ResponseEntity<>(savedCrop, HttpStatus.CREATED);
    }

    // Eliminar un cultivo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (cropService.delete(id)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/{id}/estado")
    public ResponseEntity<Crop> updateCropStatus(
            @PathVariable Long id,
            @RequestParam String estado,
            Authentication authentication) {

        String emailAgricultor = authentication.getName();

        return cropService.updateStatus(id, estado, emailAgricultor)
                .map(crop -> new ResponseEntity<>(crop, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}