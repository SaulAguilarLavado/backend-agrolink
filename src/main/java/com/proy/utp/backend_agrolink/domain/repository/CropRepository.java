package com.proy.utp.backend_agrolink.domain.repository;

import com.proy.utp.backend_agrolink.domain.Crop;

import java.util.List;
import java.util.Optional;

public interface CropRepository {
    List<Crop> getAll();
    Optional<Crop> findById(Long id); // <-- Nombre estándar
    List<Crop> findByFarmerId(Long farmerId); // <-- Nombre estándar
    Crop save(Crop crop);
    void deleteById(Long id); // <-- Nombre estándar
}