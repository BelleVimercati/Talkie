package com.tcc.talkie.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.talkie.domain.category.Subcategory;
import com.tcc.talkie.dto.ErrorResponse;
import com.tcc.talkie.dto.request.SubcategoryCreateDTO;
import com.tcc.talkie.dto.response.ApiResponse;
import com.tcc.talkie.dto.response.SubcategoryResponseDTO;
import com.tcc.talkie.service.SubcategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/subcategories")
@RequiredArgsConstructor
public class SubcategoryController {

    private final SubcategoryService service;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid SubcategoryCreateDTO dto){
        Subcategory newSub = service.create(dto);
        return ResponseEntity.ok(new ApiResponse<>("Subcategoria criada com sucesso", new SubcategoryResponseDTO(newSub.getName(), newSub.getCategory().getName(), newSub.getId())));

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        Subcategory sub = service.findById(id);
        return ResponseEntity.ok(new SubcategoryResponseDTO(
            sub.getName(),
            sub.getCategory().getName(),
            sub.getId()
        ));
    }

    @GetMapping
    public ResponseEntity<List<SubcategoryResponseDTO>> getAll(){
        List<SubcategoryResponseDTO> subcategories = service.findAll()
        .stream()
        .map(sub -> new SubcategoryResponseDTO(
            sub.getName(),
            sub.getCategory().getName(),
            sub.getId()
        ))
        .toList();

        return ResponseEntity.ok(subcategories);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.ok(new ErrorResponse("Subcategoria deletada com sucesso", 200, LocalDate.now().toString()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid SubcategoryCreateDTO dto){
        Subcategory sub = service.update(id, dto);
        return ResponseEntity.ok( new ApiResponse<>("Subcategoria atualizada com sucesso", new SubcategoryResponseDTO(sub.getName(), sub.getCategory().getName(), sub.getId())));
    }
}
