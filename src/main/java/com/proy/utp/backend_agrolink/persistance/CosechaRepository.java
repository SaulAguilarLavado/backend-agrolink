package com.proy.utp.backend_agrolink.persistance;

import com.proy.utp.backend_agrolink.domain.Harvest;
import com.proy.utp.backend_agrolink.domain.repository.HarvestRepository;
import com.proy.utp.backend_agrolink.persistance.crud.CosechaCrudRepository;
import com.proy.utp.backend_agrolink.persistance.crud.CultivoCrudRepository;
import com.proy.utp.backend_agrolink.persistance.entity.Cosecha;
import com.proy.utp.backend_agrolink.persistance.entity.Cultivo;
import com.proy.utp.backend_agrolink.persistance.mapper.HarvestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CosechaRepository implements HarvestRepository {

    @Autowired private CosechaCrudRepository cosechaCrudRepository;
    @Autowired private CultivoCrudRepository cultivoCrudRepository; // Necesario para buscar el cultivo
    @Autowired private HarvestMapper mapper;

    @Override
    public Harvest save(Harvest harvest) {
        // 1. Buscamos la entidad Cultivo a la que pertenece esta cosecha
        Cultivo cultivo = cultivoCrudRepository.findById(harvest.getCropId())
                .orElseThrow(() -> new RuntimeException("Cultivo no encontrado para la cosecha"));

        // 2. Convertimos el objeto de dominio Harvest a la entidad Cosecha
        Cosecha cosecha = mapper.toCosecha(harvest);

        // 3. Establecemos la relación entre Cosecha y Cultivo
        cosecha.setCultivo(cultivo);

        // 4. Guardamos la entidad Cosecha y devolvemos el resultado mapeado
        return mapper.toHarvest(cosechaCrudRepository.save(cosecha));
    }

    @Override
    public Optional<Harvest> findById(Long id) {
        return cosechaCrudRepository.findById(id).map(mapper::toHarvest);
    }

    @Override
    public List<Harvest> findByCropId(Long cropId) {
        return mapper.toHarvests(cosechaCrudRepository.findByCultivoId(cropId));
    }
}