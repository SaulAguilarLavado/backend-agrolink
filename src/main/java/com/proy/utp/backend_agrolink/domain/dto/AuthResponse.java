package com.proy.utp.backend_agrolink.domain.dto;

import java.util.List;

public class AuthResponse {

    private String accessToken;
    private String tokenType = "Bearer";

    private String email;
    private String name;
    private String lastname;
    private List<String> roles;

    public AuthResponse(String accessToken, String email, String name, String lastname, List<String> roles) {
        this.accessToken = accessToken;
        this.email = email;
        this.name = name;
        this.lastname = lastname;
        this.roles = roles;
    }

    // Getters y setters
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}