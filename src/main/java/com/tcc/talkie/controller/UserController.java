/* 
TODO: Implementar endpoint de Delete
TODO: transferir a lógica para camada de serviço
*/

package com.tcc.talkie.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.UpdateDTO;
import com.tcc.talkie.dto.UserResponseDTO;
import com.tcc.talkie.repository.UserRepository;
import com.tcc.talkie.service.UserService;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository repository;
    private final UserService service;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getUsers(){

        List<UserResponseDTO> users = repository.findAll()
        .stream()
            .map(user -> new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail()
            ))
            .toList();

        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody UpdateDTO data){
        User user = service.updateUser(id, data);
        return ResponseEntity.ok(new UserResponseDTO(
            user.getId(),
            user.getName(),
            user.getEmail()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id){
        User user = service.getUserById(id);
        return ResponseEntity.ok(new UserResponseDTO(
            user.getId(),
            user.getName(),
            user.getEmail()));
    }
    
}
            