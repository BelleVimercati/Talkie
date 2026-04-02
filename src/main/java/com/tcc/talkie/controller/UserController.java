/* 
TODO: Implementar endpoint de Delete
TODO: transferir a lógica para camada de serviço
*/

package com.tcc.talkie.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.ErrorResponse;
import com.tcc.talkie.dto.UpdateDTO;
import com.tcc.talkie.dto.UserResponseDTO;
import com.tcc.talkie.repository.UserRepository;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
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
        Optional<User> userOptional = repository.findById(id);

        if(userOptional.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        User user = userOptional.get();
        user.setName(data.name());
        user.setEmail(data.email());

        repository.save(user);
        return ResponseEntity.ok(new ErrorResponse("Usuário atualizado com sucesso", 200, LocalDate.now().toString()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id){
        Optional<User> userOptional = repository.findById(id);

        if(userOptional.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new UserResponseDTO(
            userOptional.get().getId(),
            userOptional.get().getName(),
            userOptional.get().getEmail()
        ));
    }
    
}
