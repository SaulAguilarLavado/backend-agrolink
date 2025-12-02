package com.proy.utp.backend_agrolink.domain.dto;

import com.proy.utp.backend_agrolink.domain.Role;
import com.proy.utp.backend_agrolink.domain.User;
import java.util.Set;
import java.util.stream.Collectors;

public class AdminUserDto {
    private Long userId;
    private String name;
    private String email;
    private Set<String> roles;

    public AdminUserDto(User user) {
        this.userId = user.getUserId();
        this.name = user.getName() + " " + user.getLastname();
        this.email = user.getEmail();
        this.roles = user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet());
    }
    // Genera Getters y Setters

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}