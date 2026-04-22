package com.tcc.talkie.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.UpdateDTO;
import com.tcc.talkie.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public User updateUser(UUID id, UpdateDTO data){
        try{
            User user = repository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    
            user.setName(data.name());
            user.setEmail(data.email());
    
            return repository.save(user);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public User getUserById(UUID id){
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public List<User> getAllUsers(){
        return repository.findAll();
    }

    public void deleteUser(UUID id){
        try{
            User user = repository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            repository.delete(user);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    
}
