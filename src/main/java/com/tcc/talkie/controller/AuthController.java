package com.tcc.talkie.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.RegisterDTO;
import com.tcc.talkie.infra.security.hash.HashUtil;
import com.tcc.talkie.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository repository;

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterDTO data){

        String cpfLimpo = data.cpf().replaceAll("\\D", ""); 

        if(repository.existsByCpfHash(HashUtil.hashCpf(cpfLimpo))){
            return ResponseEntity.badRequest().body("CPF já cadastrado");
        }

        User user = new User();

        user.setName(data.name());
        user.setEmail(data.email());
        user.setPassword(data.password());
        user.setCpf(cpfLimpo);
        user.setCpfHash(HashUtil.hashCpf(cpfLimpo));

        repository.save(user);

        return ResponseEntity.ok("Usuário criado com sucesso");
    }
    
}
