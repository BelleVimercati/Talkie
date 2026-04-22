package com.tcc.talkie.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.UpdateDTO;
import com.tcc.talkie.infra.exceptions.NotFoundException;
import com.tcc.talkie.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public User updateUser(UUID id, UpdateDTO data){
        User user = repository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        user.setName(data.name());
        user.setEmail(data.email());
        return repository.save(user);
    }

    public User getUserById(UUID id){
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    public List<User> getAllUsers(){
        return repository.findAll();
    }

    public void deleteUser(UUID id){
        User user = repository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        repository.delete(user);
    }
    
}
