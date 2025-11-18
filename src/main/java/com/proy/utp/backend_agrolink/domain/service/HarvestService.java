package com.proy.utp.backend_agrolink.domain.service;

import com.proy.utp.backend_agrolink.domain.Crop;
import com.proy.utp.backend_agrolink.domain.Harvest;
import com.proy.utp.backend_agrolink.domain.dto.HarvestRegistrationRequest;
import com.proy.utp.backend_agrolink.domain.repository.CropRepository;
import com.proy.utp.backend_agrolink.domain.repository.HarvestRepository;
import com.proy.utp.backend_agrolink.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HarvestService {

    @Autowired
    private HarvestRepository harvestRepository;

    @Autowired
    private CropRepository cropRepository; // Necesario para la validación de seguridad

    @Autowired
    private UserRepository userRepository;

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

    /**
     * Devuelve la lista de cosechas del agricultor identificado por su email.
     */
    public java.util.List<Harvest> findByFarmerEmail(String farmerEmail) {
        return userRepository.findByEmail(farmerEmail)
                .map(user -> harvestRepository.findByFarmerId(user.getUserId()))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + farmerEmail));
    }

    /**
     * Actualiza una cosecha existente (solo el agricultor propietario puede hacerlo).
     */
    @Transactional
    public Harvest updateHarvest(Long id, HarvestRegistrationRequest dto, String farmerEmail) {
        Harvest existing = harvestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cosecha no encontrada: " + id));

        Crop crop = cropRepository.findById(existing.getCropId())
                .orElseThrow(() -> new RuntimeException("Cultivo no encontrado para la cosecha"));

        if (!crop.getFarmer().getEmail().equals(farmerEmail)) {
            throw new SecurityException("No tienes permiso para actualizar esta cosecha.");
        }

        // Aplicar cambios
        existing.setHarvestDate(dto.getHarvestDate());
        existing.setQuantityHarvested(dto.getQuantityHarvested());
        existing.setUnitOfMeasure(dto.getUnitOfMeasure());
        existing.setQualityNotes(dto.getQualityNotes());

        return harvestRepository.save(existing);
    }

    /**
     * Elimina una cosecha si el agricultor autenticado es el propietario del cultivo.
     */
    @Transactional
    public void deleteHarvest(Long id, String farmerEmail) {
        Harvest existing = harvestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cosecha no encontrada: " + id));

        Crop crop = cropRepository.findById(existing.getCropId())
                .orElseThrow(() -> new RuntimeException("Cultivo no encontrado para la cosecha"));

        if (!crop.getFarmer().getEmail().equals(farmerEmail)) {
            throw new SecurityException("No tienes permiso para eliminar esta cosecha.");
        }

        harvestRepository.deleteById(id);
    }
}