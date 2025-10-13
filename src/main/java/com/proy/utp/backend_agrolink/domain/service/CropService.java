package com.proy.utp.backend_agrolink.domain.service;

import com.proy.utp.backend_agrolink.domain.dto.CropDTO;
import com.proy.utp.backend_agrolink.domain.repository.CropRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CropService {

    @Autowired
    CropRepository cropRepository;

    public List<CropDTO> getAll() {
        return cropRepository.getAll();
    }

    public Optional<CropDTO> getCrop(Long id){
        return  cropRepository.getCrop(id);
    }

    public List<CropDTO> getByFarmer(Long farmerId){
        return cropRepository.getByFarmer(farmerId);
    }

    public CropDTO save(CropDTO crop){
        return cropRepository.save(crop);
    }

    public boolean delete(Long id){
        return getCrop(id).map(cropDTO -> {
            cropRepository.delete(id);
            return true;
        }).orElse(false);
    }
}
