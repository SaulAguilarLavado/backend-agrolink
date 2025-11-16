package com.proy.utp.backend_agrolink.domain.repository;

import com.proy.utp.backend_agrolink.domain.Harvest;
import java.util.List;
import java.util.Optional;

public interface HarvestRepository {
    Harvest save(Harvest harvest);
    Optional<Harvest> findById(Long id);
    List<Harvest> findByCropId(Long cropId);
}