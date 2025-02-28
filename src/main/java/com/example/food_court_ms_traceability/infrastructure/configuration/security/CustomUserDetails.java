package com.example.food_court_ms_traceability.infrastructure.configuration.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private final String username;
    private final String documentNumber;
    private final String nit;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(String username, String documentNumber, String nit, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.documentNumber = documentNumber;
        this.nit = nit;
        this.authorities = authorities;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getNit() {
        return nit;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}