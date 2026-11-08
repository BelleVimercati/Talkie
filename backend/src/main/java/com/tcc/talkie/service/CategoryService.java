package com.tcc.talkie.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.request.CategoryCreateDTO;
import com.tcc.talkie.dto.response.CategoryResponseDTO;
import com.tcc.talkie.infra.exceptions.BadRequestException;
import com.tcc.talkie.infra.exceptions.NotFoundException;
import com.tcc.talkie.infra.security.AuthenticatedUser;
import com.tcc.talkie.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    public List<Category> findAll(boolean includeInactive){
        return includeInactive ? repository.findAll() : repository.findByActiveTrue();
    }

    public Category create(CategoryCreateDTO dto){
        String name = validateName(dto.name());
        validateUniqueName(name, null);

        User user = AuthenticatedUser.get();

        Category type = new Category();
        type.setName(name);
        type.setIcon(dto.icon());
        type.setPriority(dto.priority());
        type.setColor(dto.color());
        type.setCreatedAt(LocalDateTime.now());
        type.setUser(user);

        return repository.save(type);
    }

    public Category update(Long id, CategoryCreateDTO data){
        Category category = findById(id);
        String name = validateName(data.name());
        validateUniqueName(name, id);

        category.setName(name);
        category.setIcon(data.icon());
        category.setPriority(data.priority());
        category.setColor(data.color());

        return repository.save(category);
    }

    /* Exclusão lógica: ocorrências e assinaturas existentes continuam apontando para a categoria */
    @Transactional
    public Category deactivate(Long id){
        Category category = findById(id);
        category.setActive(false);
        return repository.save(category);
    }

    @Transactional
    public Category reactivate(Long id){
        Category category = findById(id);
        category.setActive(true);
        return repository.save(category);
    }

    public Category findById(Long id){
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Categoria não encontrada"));
    }

    public CategoryResponseDTO toResponse(Category c){
        return new CategoryResponseDTO(
            c.getId(),
            c.getName(),
            c.getIcon(),
            c.getUser() != null ? c.getUser().getId() : null,
            c.getPriority(),
            c.getColor(),
            c.getCreatedAt(),
            c.isActive()
        );
    }

    private String validateName(String name){
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Nome da categoria é obrigatório");
        }
        return name.trim();
    }

    private void validateUniqueName(String name, Long currentId){
        repository.findByNameIgnoreCase(name)
            .filter(existing -> !existing.getId().equals(currentId))
            .ifPresent(existing -> {
                throw new BadRequestException(existing.isActive()
                    ? "Já existe uma categoria com esse nome"
                    : "Já existe uma categoria inativa com esse nome; reative-a");
            });
    }
}
