package com.tcc.talkie.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.category.Subcategory;
import com.tcc.talkie.dto.request.SubcategoryCreateDTO;
import com.tcc.talkie.dto.response.SubcategoryResponseDTO;
import com.tcc.talkie.infra.exceptions.BadRequestException;
import com.tcc.talkie.infra.exceptions.NotFoundException;
import com.tcc.talkie.repository.CategoryRepository;
import com.tcc.talkie.repository.SubcategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubcategoryService {

    private final SubcategoryRepository repository;
    private final CategoryRepository categoryRepository;

    public Subcategory create(SubcategoryCreateDTO dto){
        String name = validateName(dto.name());
        Category category = findActiveCategory(dto.categoryId());
        validateUniqueName(name, category.getId(), null);

        Subcategory sub = new Subcategory();
        sub.setName(name);
        sub.setCategory(category);

        return repository.save(sub);
    }

    /* Padrão: só subcategorias ativas de categorias ativas (as que aparecem nos formulários) */
    public List<Subcategory> findAll(boolean includeInactive){
        return includeInactive ? repository.findAll() : repository.findByActiveTrueAndCategoryActiveTrue();
    }

    public Subcategory update(Long id, SubcategoryCreateDTO dto){
        Subcategory sub = findById(id);
        String name = validateName(dto.name());

        // Manter a categoria atual é permitido mesmo se ela estiver inativa; mover exige categoria ativa
        Category category = sub.getCategory().getId().equals(dto.categoryId())
            ? sub.getCategory()
            : findActiveCategory(dto.categoryId());
        validateUniqueName(name, category.getId(), id);

        sub.setName(name);
        sub.setCategory(category);

        return repository.save(sub);
    }

    /* Exclusão lógica: ocorrências existentes continuam apontando para a subcategoria */
    @Transactional
    public Subcategory deactivate(Long id){
        Subcategory sub = findById(id);
        sub.setActive(false);
        return repository.save(sub);
    }

    @Transactional
    public Subcategory reactivate(Long id){
        Subcategory sub = findById(id);
        if (!sub.getCategory().isActive()) {
            throw new BadRequestException("Reative a categoria antes de reativar a subcategoria");
        }
        sub.setActive(true);
        return repository.save(sub);
    }

    public Subcategory findById(Long id){
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Subcategoria não encontrada"));
    }

    public SubcategoryResponseDTO toResponse(Subcategory sub){
        return new SubcategoryResponseDTO(
            sub.getName(),
            sub.getCategory().getName(),
            sub.getId(),
            sub.getCategory().getId(),
            sub.isActive()
        );
    }

    private Category findActiveCategory(Long categoryId){
        if (categoryId == null) {
            throw new BadRequestException("Categoria é obrigatória");
        }
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));
        if (!category.isActive()) {
            throw new BadRequestException("Categoria inativa");
        }
        return category;
    }

    private String validateName(String name){
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Nome da subcategoria é obrigatório");
        }
        return name.trim();
    }

    private void validateUniqueName(String name, Long categoryId, Long currentId){
        repository.findByNameIgnoreCaseAndCategoryId(name, categoryId)
            .filter(existing -> !existing.getId().equals(currentId))
            .ifPresent(existing -> {
                throw new BadRequestException(existing.isActive()
                    ? "Já existe uma subcategoria com esse nome nesta categoria"
                    : "Já existe uma subcategoria inativa com esse nome nesta categoria; reative-a");
            });
    }
}
