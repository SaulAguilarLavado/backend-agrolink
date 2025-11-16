package com.proy.utp.backend_agrolink.domain.service;

import com.proy.utp.backend_agrolink.domain.Crop;
import com.proy.utp.backend_agrolink.domain.Harvest;
import com.proy.utp.backend_agrolink.domain.dto.HarvestRegistrationRequest;
import com.proy.utp.backend_agrolink.domain.repository.CropRepository;
import com.proy.utp.backend_agrolink.domain.repository.HarvestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HarvestService {

    @Autowired
    private HarvestRepository harvestRepository;

    @Autowired
    private CropRepository cropRepository; // Necesario para la validación de seguridad

    /**
     * Registra una nueva cosecha y valida que el agricultor sea el dueño del cultivo.
     * @param request Los datos de la nueva cosecha.
     * @param farmerEmail El email del agricultor autenticado.
     * @return La cosecha guardada.
     */
    public Harvest registerHarvest(HarvestRegistrationRequest request, String farmerEmail) {
        // 1. Validación de Seguridad: Verificar que el cultivo pertenece al agricultor
        Crop crop = cropRepository.findById(request.getCropId())
                .orElseThrow(() -> new RuntimeException("El cultivo con ID " + request.getCropId() + " no existe."));

        if (!crop.getFarmer().getEmail().equals(farmerEmail)) {
            // Si el email del dueño del cultivo no coincide con el del usuario logueado, se lanza una excepción.
            throw new SecurityException("No tienes permiso para registrar una cosecha en este cultivo.");
        }

        // 2. Crear el objeto de dominio a partir del DTO
        Harvest newHarvest = new Harvest();
        newHarvest.setCropId(request.getCropId());
        newHarvest.setHarvestDate(request.getHarvestDate());
        newHarvest.setQuantityHarvested(request.getQuantityHarvested());
        newHarvest.setUnitOfMeasure(request.getUnitOfMeasure());
        newHarvest.setQualityNotes(request.getQualityNotes());

        // 3. Llamar al repositorio para guardar la cosecha
        return harvestRepository.save(newHarvest);
    }
}