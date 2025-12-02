package com.proy.utp.backend_agrolink.domain.service;

import com.proy.utp.backend_agrolink.domain.dto.AdminUserDto;
import com.proy.utp.backend_agrolink.domain.repository.UserRepository;
import com.proy.utp.backend_agrolink.persistance.crud.TransaccionCrudRepository;
import com.proy.utp.backend_agrolink.persistance.entity.Transaccion;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final TransaccionCrudRepository transaccionRepository;

    public AdminService(UserRepository userRepository, TransaccionCrudRepository transaccionRepository) {
        this.userRepository = userRepository;
        this.transaccionRepository = transaccionRepository;
    }

    public List<AdminUserDto> getAllUsers() {
        return userRepository.findAll().stream().map(AdminUserDto::new).collect(Collectors.toList());
    }

    public List<Transaccion> getSalesReportByPeriod(String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;
        switch (period.toLowerCase()) {
            case "week": startDate = now.minusWeeks(1); break;
            case "month": startDate = now.minusMonths(1); break;
            case "year": startDate = now.minusYears(1); break;
            default: throw new IllegalArgumentException("Periodo no válido: " + period);
        }
        return transaccionRepository.findByFechaTransaccionBetween(startDate, now);
    }
}