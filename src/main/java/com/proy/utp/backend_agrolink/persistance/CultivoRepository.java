package com.proy.utp.backend_agrolink.persistance;

import com.proy.utp.backend_agrolink.domain.Crop;
import com.proy.utp.backend_agrolink.domain.repository.CropRepository;
import com.proy.utp.backend_agrolink.persistance.crud.CultivoCrudRepository;
import com.proy.utp.backend_agrolink.persistance.entity.Cultivo;
import com.proy.utp.backend_agrolink.persistance.mapper.CropMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CultivoRepository implements CropRepository {

    @Autowired
    private CultivoCrudRepository cultivoCrudRepository;

    @Autowired
    private CropMapper mapper;

    @Override
    public List<Crop> getAll() {
        List<Cultivo> cultivos = (List<Cultivo>) cultivoCrudRepository.findAll();
        // Correcto: Mapea a una lista de objetos de dominio 'Crop'
        return mapper.toCrops(cultivos);
    }

    // --- CORRECCIÓN 1: El nombre del método debe ser 'findById' ---
    @Override
    public Optional<Crop> findById(Long id) {
        return cultivoCrudRepository.findById(id)
                // Correcto: Mapea a un objeto de dominio 'Crop'
                .map(mapper::toCrop);
    }

    // --- CORRECCIÓN 2: El nombre del método debe ser 'findByFarmerId' ---
    @Override
    public List<Crop> findByFarmerId(Long farmerId) {
        List<Cultivo> cultivos = cultivoCrudRepository.findByAgricultorIdOrderByFechaSiembraDesc(farmerId);
        // Correcto: Mapea a una lista de objetos de dominio 'Crop'
        return mapper.toCrops(cultivos);
    }

    @Override
    public Crop save(Crop crop) {
        // Correcto: Convierte el objeto de dominio 'Crop' a la entidad 'Cultivo'
        Cultivo cultivo = mapper.toCultivo(crop);
        // Guardamos la entidad y luego convertimos el resultado de vuelta a 'Crop'
        return mapper.toCrop(cultivoCrudRepository.save(cultivo));
    }

    // --- CORRECCIÓN 3: El nombre del método debe ser 'deleteById' ---
    @Override
    public void deleteById(Long id) {
        cultivoCrudRepository.deleteById(id);
    }
}