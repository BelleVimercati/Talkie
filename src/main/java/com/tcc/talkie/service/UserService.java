package com.tcc.talkie.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.UpdateDTO;
import com.tcc.talkie.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public User updateUser(UUID id, UpdateDTO data){
        User user = repository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(data.name());
        user.setEmail(data.email());

        return repository.save(user);
    }

    public User getUserById(UUID id){
        return repository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers(){
        return repository.findAll();
    }
}
