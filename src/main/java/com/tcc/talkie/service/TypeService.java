package com.tcc.talkie.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tcc.talkie.domain.type.Type;
import com.tcc.talkie.dto.ErrorResponse;
import com.tcc.talkie.repository.TypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TypeService {

    private final TypeRepository repository;

    public List<Type> findAll(){
        return repository.findAll();
    }

    public Type create(Type type){
       if (repository.findByName(type.getName().trim()).isPresent()) {
            throw new IllegalArgumentException("Tipo já existe");
        }
        return repository.save(type);
    }

    public void delete(Long id){
        if (repository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Tipo não encontrado");
        }
        repository.deleteById(id);
    }
}
