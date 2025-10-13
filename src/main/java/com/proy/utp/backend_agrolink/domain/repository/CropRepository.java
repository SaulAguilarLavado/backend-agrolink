package com.proy.utp.backend_agrolink.domain.repository;

import com.proy.utp.backend_agrolink.domain.dto.CropDTO;

import java.util.List;
import java.util.Optional;

public interface CropRepository {
    List<CropDTO> getAll();
    Optional<CropDTO> getCrop(Long id);
    List<CropDTO> getByFarmer(Long farmerId);
    CropDTO save(CropDTO crop);
    void delete(Long id);
}
