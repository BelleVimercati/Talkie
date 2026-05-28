package com.tcc.talkie.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.dto.ErrorResponse;
import com.tcc.talkie.dto.request.CategoryCreateDTO;
import com.tcc.talkie.dto.response.CategoryResponseDTO;
import com.tcc.talkie.service.CategoryService;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getTypes(){
        List<CategoryResponseDTO> types = service.findAll()
        .stream()
        .map(type -> new CategoryResponseDTO(
            type.getId(),
            type.getName(),
            type.getIcon(),
            type.getUser().getId()
        ))
        .toList();

        return ResponseEntity.ok(types);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CategoryCreateDTO data){
        Category created = service.create(data);
        return ResponseEntity.ok(new CategoryResponseDTO(
            created.getId(),
            created.getName(),
            created.getIcon(),
            created.getUser().getId()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.ok(new ErrorResponse("Categoria deletada com sucesso", 200, LocalDate.now().toString()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CategoryCreateDTO data){
        Category updated = service.update(id, data);
        return ResponseEntity.ok(new CategoryResponseDTO(
            updated.getId(),
            updated.getName(),
            updated.getIcon(),
            updated.getUser().getId()
        ));

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
            Category category = service.findById(id);
            return ResponseEntity.ok(new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getIcon(),
                category.getUser().getId()
            ));
    }
}
