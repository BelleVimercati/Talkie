package com.tcc.talkie.infra.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/* Filtro de segurança para validar tokens JWT */
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    public SecurityFilter(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        var token = this.recoverToken(request);

        if (token != null) {
            var login = tokenService.validateToken(token);
            var role = tokenService.getRole(token);

            if (login != null && role != null) {

                User user = userRepository.findByEmail(login)
                    .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado"));

                var authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + role)
                );

                var authentication = new UsernamePasswordAuthenticationToken(
                    user, 
                    null,
                    authorities
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if(authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }


}

/* 
        Requisição HTTP
            ↓
        recoverToken()  →  extrai o token do header
            ↓
        tokenService.validateToken()  →  valida e retorna o login (email)
            ↓
            login != null?
            /         \
        SIM         NÃO
            ↓            ↓
        busca user    segue sem autenticar
        no banco
            ↓
        cria autenticação
            ↓
        injeta no SecurityContext
            ↓
        filterChain.doFilter()  →  passa pro próximo filtro/controller
 */