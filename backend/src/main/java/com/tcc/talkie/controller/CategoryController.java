package com.tcc.talkie.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.tcc.talkie.dto.request.CategoryCreateDTO;
import com.tcc.talkie.dto.response.ApiResponse;
import com.tcc.talkie.dto.response.CategoryResponseDTO;
import com.tcc.talkie.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getAll(
        @RequestParam(defaultValue = "false") boolean includeInactive
    ){
        List<CategoryResponseDTO> categories = service.findAll(includeInactive)
            .stream()
            .map(service::toResponse)
            .toList();
        return ResponseEntity.ok(new ApiResponse<>("Categorias recuperadas com sucesso", categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> getById(@PathVariable Long id){
        return ResponseEntity.ok(new ApiResponse<>("Categoria recuperada com sucesso", service.toResponse(service.findById(id))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> create(@Valid @RequestBody CategoryCreateDTO data){
        return ResponseEntity.ok(new ApiResponse<>("Categoria criada com sucesso", service.toResponse(service.create(data))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody CategoryCreateDTO data){
        return ResponseEntity.ok(new ApiResponse<>("Categoria atualizada com sucesso", service.toResponse(service.update(id, data))));
    }

    /* Exclusão lógica: a categoria é desativada, não apagada */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> deactivate(@PathVariable Long id){
        return ResponseEntity.ok(new ApiResponse<>("Categoria desativada com sucesso", service.toResponse(service.deactivate(id))));
    }

    @PutMapping("/{id}/reactivate")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> reactivate(@PathVariable Long id){
        return ResponseEntity.ok(new ApiResponse<>("Categoria reativada com sucesso", service.toResponse(service.reactivate(id))));
    }
}
