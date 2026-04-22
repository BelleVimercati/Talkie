package com.tcc.talkie.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.request.CategoryCreateDTO;
import com.tcc.talkie.repository.CategoryRepository;
import com.tcc.talkie.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;
    private final UserRepository userRepository;

    public List<Category> findAll(){
        return repository.findAll();
    }

    public Category create(CategoryCreateDTO dto){

        if (repository.findByName(dto.name().trim()).isPresent()) {
            throw new IllegalArgumentException("Tipo já existe");
        }

        User user = userRepository.findById(dto.userId())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Category type = new Category();
        type.setName(dto.name());
        type.setIcon(dto.icon());
        type.setUser(user);

        return repository.save(type);
    }

    public void delete(Long id){
        if (repository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Tipo não encontrado");
        }
        repository.deleteById(id);
    }
}
