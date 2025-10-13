package com.proy.utp.backend_agrolink.web.controller;

import com.proy.utp.backend_agrolink.domain.dto.CropDTO;
import com.proy.utp.backend_agrolink.domain.service.CropService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cultivos")
public class CropController {

    @Autowired
    private CropService cropService;

    // Obtener todos los cultivos
    @GetMapping("/all")
    public List<CropDTO> getAll() {
        return cropService.getAll();
    }

    // Obtener un cultivo por su ID
    @GetMapping("/{id}")
    public CropDTO getCrop(@PathVariable Long id) {
        Optional<CropDTO> crop = cropService.getCrop(id);
        return crop.orElse(null); // Devuelve null si no existe (Spring lo maneja como 200 con cuerpo vacío)
    }

    // Obtener cultivos por agricultor
    @GetMapping("/agricultor/{idAgricultor}")
    public List<CropDTO> getByFarmer(@PathVariable("idAgricultor") Long idAgricultor) {
        return cropService.getByFarmer(idAgricultor);
    }

    // Guardar un nuevo cultivo
    @PostMapping("/save")
    public CropDTO save(@RequestBody CropDTO crop) {
        return cropService.save(crop);
    }

    // Eliminar un cultivo
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        cropService.delete(id);
    }
}

