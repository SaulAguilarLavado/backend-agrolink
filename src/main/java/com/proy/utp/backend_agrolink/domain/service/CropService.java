package com.proy.utp.backend_agrolink.domain.service;

import com.proy.utp.backend_agrolink.domain.Crop;
import com.proy.utp.backend_agrolink.domain.User;
import com.proy.utp.backend_agrolink.domain.repository.CropRepository;
import com.proy.utp.backend_agrolink.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CropService {

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Crop> getAll() {
        return cropRepository.getAll();
    }

    public Optional<Crop> findById(Long id) {
        // Correcto: Llama a findById en el repositorio
        return cropRepository.findById(id);
    }

    public List<Crop> findByAuthenticatedFarmer(String farmerEmail) {
        User farmer = userRepository.findByEmail(farmerEmail)
                .orElseThrow(() -> new RuntimeException("Agricultor no encontrado"));
        // Correcto: Llama a findByFarmerId en el repositorio
        return cropRepository.findByFarmerId(farmer.getUserId());
    }

    public Crop saveForAuthenticatedFarmer(Crop crop, String farmerEmail) {
        User farmer = userRepository.findByEmail(farmerEmail)
                .orElseThrow(() -> new RuntimeException("Agricultor no encontrado"));
        crop.setFarmer(farmer);
        // Correcto: Llama a save en el repositorio
        return cropRepository.save(crop);
    }

    public boolean delete(Long id) {
        return findById(id).map(crop -> {
            // Correcto: Llama a deleteById en el repositorio
            cropRepository.deleteById(id);
            return true;
        }).orElse(false);
    }
    public Optional<Crop> updateStatus(Long id, String nuevoEstado, String emailAgricultor) {
        return cropRepository.findById(id)
                .map(crop -> {
                    // Validar que el cultivo pertenezca al agricultor autenticado
                    if (!crop.getFarmer().getEmail().equals(emailAgricultor)) {
                        throw new RuntimeException("No tienes permiso para modificar este cultivo");
                    }

                    crop.setStatus(nuevoEstado);

                    // ✅ Si el estado es “Cosechado”, ponemos la fecha actual
                    if ("Cosechado".equalsIgnoreCase(nuevoEstado)) {
                        crop.setHarvestDate(LocalDate.now());
                    }

                    return cropRepository.save(crop);
                });
    }
}