package com.tcc.talkie.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.category.Subcategory;
import com.tcc.talkie.dto.request.SubcategoryCreateDTO;
import com.tcc.talkie.repository.CategoryRepository;
import com.tcc.talkie.repository.SubcategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubcategoryService {

    private final SubcategoryRepository repository;
    private final CategoryRepository categoryRepository;

    public Subcategory create(SubcategoryCreateDTO dto){
        Category category = categoryRepository.findById(dto.categoryId())
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

            Subcategory sub = new Subcategory();
            sub.setName(dto.name());
            sub.setCategory(category);

            return repository.save(sub);
    }

    public List<Subcategory> findAll(){
        return repository.findAll();
    }

    public void delete(Long id){
        try{
            Subcategory sub = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subcategoria não encontrada"));

            repository.deleteById(id);
        } catch (Exception e){
            throw new RuntimeException("Subcategoria não encontrada");
        }
    }

    public void update(Long id, SubcategoryCreateDTO dto){
        try{
            Subcategory sub = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subcategoria não encontrada"));

            Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

            sub.setName(dto.name());
            sub.setCategory(category);

            repository.save(sub);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public Subcategory findById(Long id){
        try{
            return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subcategoria não encontrada"));
        } catch (Exception e){
            throw new RuntimeException("Subcategoria não encontrada");
        }
    }
}
