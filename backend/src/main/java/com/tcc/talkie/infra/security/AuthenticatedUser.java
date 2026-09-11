package com.tcc.talkie.infra.security;

import java.util.UUID;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.infra.exceptions.UnauthorizedException;

public class AuthenticatedUser {

    private AuthenticatedUser() {}

    public static User get() {

        Authentication auth =
            SecurityContextHolder.getContext().getAuthentication();

        if (auth == null ||
            !auth.isAuthenticated() ||
            auth instanceof AnonymousAuthenticationToken) {

            throw new UnauthorizedException("Usuário não autenticado");
        }

        return (User) auth.getPrincipal();
    }

    public static UUID getId(){
        return get().getId();
    }

    public static String getEmail(){
        return get().getEmail();
    }

    public static String getName(){
        return get().getName();
    }
}
