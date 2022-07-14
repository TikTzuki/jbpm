package org.minerva.stateservice.controllers;

import org.kie.internal.identity.IdentityProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HyperIdentityProvider implements IdentityProvider {

    @Override
    public String getName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "system";
    }

    @Override
    public List<String> getRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            List<String> roles = new ArrayList<>();
            for (GrantedAuthority ga : auth.getAuthorities()) {
                roles.add(ga.getAuthority());
            }
            return roles;
        }
        return new ArrayList<>();
    }

    @Override
    public boolean hasRole(String role) {
        return false;
    }
}
