package com.tcc.talkie.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.request.LoginRequestDTO;
import com.tcc.talkie.dto.request.RegisterDTO;
import com.tcc.talkie.dto.response.ApiResponse;
import com.tcc.talkie.dto.response.UserResponseDTO;
import com.tcc.talkie.infra.exceptions.NotFoundException;
import com.tcc.talkie.infra.security.TokenService;
import com.tcc.talkie.repository.UserRepository;
import com.tcc.talkie.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder PasswordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO data){
        User created = authService.register(data);
        return ResponseEntity.ok( new ApiResponse<>("Usuário registrado com sucesso", 
            new UserResponseDTO(created.getId(), created.getName(), created.getEmail())
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO data){
        User user = this.userRepository.findByEmail(data.email())
            .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        if(PasswordEncoder.matches(data.password(), user.getPassword())){
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new ApiResponse<>("Login bem-sucedido", token));
        }
        return ResponseEntity.status(401).body(new ApiResponse<>("Credenciais inválidas", null));
    }
}
