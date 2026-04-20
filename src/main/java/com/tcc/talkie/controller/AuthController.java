package com.tcc.talkie.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.ErrorResponse;
import com.tcc.talkie.dto.request.RegisterDTO;
import com.tcc.talkie.infra.security.hash.HashUtil;
import com.tcc.talkie.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.crypto.password.PasswordEncoder;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterDTO data){

        String cpfLimpo = data.cpf().replaceAll("\\D", ""); 

        if(repository.existsByCpfHash(HashUtil.hashCpf(cpfLimpo))){
            return ResponseEntity.badRequest().body(new ErrorResponse("CPF já cadastrado", 400, LocalDate.now().toString()));
        }

        Optional<User> user = repository.findByEmail(data.email());

        if(user.isEmpty()){
            User newUser = new User();
    
            newUser.setName(data.name());
            newUser.setEmail(data.email());
            newUser.setPassword(passwordEncoder.encode(data.password()));
            newUser.setCpf(cpfLimpo);
            newUser.setCpfHash(HashUtil.hashCpf(cpfLimpo));
            newUser.setRole(data.role());
    
            repository.save(newUser);
            return ResponseEntity.ok(new ErrorResponse("Usuário criado com sucesso", 200, LocalDate.now().toString()));
        }
        return ResponseEntity.badRequest().body(new ErrorResponse("Email já cadastrado", 400, LocalDate.now().toString()));
    }
}
