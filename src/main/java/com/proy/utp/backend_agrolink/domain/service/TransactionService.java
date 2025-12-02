package com.proy.utp.backend_agrolink.domain.service;

import com.proy.utp.backend_agrolink.domain.User;
import com.proy.utp.backend_agrolink.domain.dto.TransactionDto;
import com.proy.utp.backend_agrolink.persistance.crud.TransaccionCrudRepository;
import com.proy.utp.backend_agrolink.persistance.entity.Transaccion;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransaccionCrudRepository transaccionRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public TransactionService(TransaccionCrudRepository transaccionRepository,
                              AuthenticatedUserService authenticatedUserService) {
        this.transaccionRepository = transaccionRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    public List<TransactionDto> getAll() {
        return transaccionRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<TransactionDto> getMySales() {
        User user = authenticatedUserService.getAuthenticatedUser();
        return transaccionRepository.findByVendedorId(user.getUserId()).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<TransactionDto> getMyPurchases() {
        User user = authenticatedUserService.getAuthenticatedUser();
        return transaccionRepository.findByCompradorId(user.getUserId()).stream().map(this::toDto).collect(Collectors.toList());
    }

    private TransactionDto toDto(Transaccion t) {
        TransactionDto dto = new TransactionDto();
        dto.setId(t.getId());
        dto.setOrderId(t.getPedido() != null ? t.getPedido().getId() : null);
        dto.setSellerEmail(t.getVendedor() != null ? t.getVendedor().getEmail() : null);
        dto.setBuyerEmail(t.getComprador() != null ? t.getComprador().getEmail() : null);
        dto.setTotalAmount(t.getMontoTotal());
        dto.setTransactionDate(t.getFechaTransaccion());
        return dto;
    }
}
