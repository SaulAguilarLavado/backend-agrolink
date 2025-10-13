package com.proy.utp.backend_agrolink.web.security;

import com.proy.utp.backend_agrolink.persistance.crud.UsuarioCrudRepository; // <-- Usamos el CrudRepository
import com.proy.utp.backend_agrolink.persistance.entity.Usuario; // <-- Usamos la Entidad
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioCrudRepository usuarioCrudRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioCrudRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + email));

        // --- HAZ ESTE CAMBIO ---
        // Convertimos el nombre del rol a mayúsculas antes de añadir el prefijo.
        Set<GrantedAuthority> authorities = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombre().toUpperCase()))
                .collect(Collectors.toSet());
        // -----------------------

        System.out.println("Usuario '" + email + "' tiene las siguientes autoridades: " + authorities);

        return new User(usuario.getEmail(), usuario.getPassword(), authorities);
    }
}