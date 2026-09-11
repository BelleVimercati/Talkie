package com.tcc.talkie.service;

import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tcc.talkie.domain.user.Role;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.request.RegisterDTO;
import com.tcc.talkie.infra.exceptions.BadRequestException;
import com.tcc.talkie.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;

    public User register(RegisterDTO data){
        String cpfLimpo = data.cpf().replaceAll("\\D", ""); 

        if(repository.existsByCpf(cpfLimpo)){
            throw new BadRequestException("CPF já cadastrado");
        }


        Optional<User> user = repository.findByEmail(data.email());

          if(user.isPresent()){
            throw new BadRequestException("Email já cadastrado");
        }

        User newUser = new User();

        newUser.setName(data.name());
        newUser.setEmail(data.email());
        newUser.setPassword(passwordEncoder.encode(data.password()));
        newUser.setCpf(cpfLimpo);
        newUser.setRole(data.role() != null ? data.role() : Role.USER);

        return repository.save(newUser);
    }
}
