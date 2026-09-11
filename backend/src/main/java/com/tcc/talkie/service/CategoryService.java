package com.tcc.talkie.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.request.CategoryCreateDTO;
import com.tcc.talkie.infra.exceptions.BadRequestException;
import com.tcc.talkie.infra.exceptions.NotFoundException;
import com.tcc.talkie.infra.security.AuthenticatedUser;
import com.tcc.talkie.repository.CategoryRepository;
import com.tcc.talkie.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    public List<Category> findAll(){
        return repository.findAll();
    }

    public Category create(CategoryCreateDTO dto){
        if (repository.findByNameIgnoreCase(dto.name().trim()).isPresent()) {
            throw new BadRequestException("Tipo já existe");
        }


        User user = AuthenticatedUser.get();

        Category type = new Category();
        type.setName(dto.name());
        type.setIcon(dto.icon());
        type.setUser(user);

        return repository.save(type);
    }

    public void delete(Long id){
        if (repository.findById(id).isEmpty()) {
            throw new NotFoundException("Tipo não encontrado");
        }
        repository.deleteById(id);
    }

    public Category update(Long id, CategoryCreateDTO data){
            Category category = repository.findById(id).orElseThrow(() -> new NotFoundException("Categoria não encontrada"));

            category.setName(data.name());
            category.setIcon(data.icon());

            return repository.save(category);
    }

    public Category findById(Long id){
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Categoria não encontrada"));
    }
}
