package com.tcc.talkie.infra.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.tcc.talkie.domain.user.User;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    /* Função para gerar token */
    public String generateToken(User user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create().withIssuer("talkie-api").withSubject(user.getEmail()).withClaim("role", user.getRole().name())
            .withExpiresAt(this.generateExpirationDate()).sign(algorithm);
            
        } catch (JWTCreationException exception){
            throw new RuntimeException("Error while generating token", exception);
        }
    }

    private Instant generateExpirationDate(){
                return LocalDateTime.now()
        .plusHours(2)
        .atZone(ZoneId.of("America/Sao_Paulo"))
        .toInstant();
    }

    /* Função para validar token */
    public String validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            
            return JWT.require(algorithm).withIssuer("talkie-api").build().verify(token).getSubject();

        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    public String getRole(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm).withIssuer("talkie-api").build().verify(token).getClaim("role").asString();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }
    
}
