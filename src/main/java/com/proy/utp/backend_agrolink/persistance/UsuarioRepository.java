package com.proy.utp.backend_agrolink.persistance;

import com.proy.utp.backend_agrolink.domain.User;
import com.proy.utp.backend_agrolink.domain.repository.UserRepository;
import com.proy.utp.backend_agrolink.persistance.crud.UsuarioCrudRepository;
import com.proy.utp.backend_agrolink.persistance.entity.Usuario;
import com.proy.utp.backend_agrolink.persistance.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UsuarioRepository implements UserRepository {
    @Autowired
    private UsuarioCrudRepository usuarioCrudRepository;

    @Autowired
    private UserMapper mapper;

    @Override
    public User save(User user) {
        Usuario usuario = mapper.toUsuario(user);
        usuario.getRoles().forEach(rol -> rol.setId(rol.getId()));
        return mapper.toUser(usuarioCrudRepository.save(usuario));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return usuarioCrudRepository.findByEmail(email)
                .map(usuario -> mapper.toUser(usuario));
    }

    @Override
    public boolean existsByEmail(String email) {
        return usuarioCrudRepository.existsByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return usuarioCrudRepository.findAll().stream()
                .map(mapper::toUser)
                .collect(Collectors.toList());
    }
}
