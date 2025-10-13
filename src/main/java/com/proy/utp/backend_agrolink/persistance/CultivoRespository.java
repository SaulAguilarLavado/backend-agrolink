package com.proy.utp.backend_agrolink.persistance;

import com.proy.utp.backend_agrolink.domain.dto.CropDTO;
import com.proy.utp.backend_agrolink.domain.repository.CropRepository;
import com.proy.utp.backend_agrolink.persistance.crud.CultivoCrudRepository;
import com.proy.utp.backend_agrolink.persistance.entity.Cultivo;
import com.proy.utp.backend_agrolink.persistance.mapper.CropMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CultivoRespository implements CropRepository {

    @Autowired
    private CultivoCrudRepository cultivoCrudRepository;

    @Autowired
    private CropMapper mapper;

    @Override
    public List<CropDTO> getAll() {
        List<Cultivo> cultivos=(List<Cultivo>) cultivoCrudRepository.findAll();
        return mapper.toCrops(cultivos);
    }

    @Override
    public Optional<CropDTO> getCrop(Long id) {
    return cultivoCrudRepository.findById(id)
            .map(cultivo -> mapper.toCropDTO(cultivo));
    }

    @Override
    public List<CropDTO> getByFarmer(Long farmerId) {
    List<Cultivo> cultivos =cultivoCrudRepository.findByAgricultorIdOrderByFechaSiembraDesc(farmerId);
    return mapper.toCrops(cultivos);
    }

    @Override
    public CropDTO save(CropDTO crop) {
        Cultivo cultivo=mapper.toCultivo(crop);
        return mapper.toCropDTO(cultivoCrudRepository.save(cultivo));
    }

    @Override
    public void delete(Long id) {
    cultivoCrudRepository.deleteById(id);
    }
}
