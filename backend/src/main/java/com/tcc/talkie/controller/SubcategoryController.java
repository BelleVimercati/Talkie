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

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubcategoryResponseDTO>>> getAll(
        @RequestParam(defaultValue = "false") boolean includeInactive
    ){
        List<SubcategoryResponseDTO> subcategories = service.findAll(includeInactive)
            .stream()
            .map(service::toResponse)
            .toList();
        return ResponseEntity.ok(new ApiResponse<>("Subcategorias recuperadas com sucesso", subcategories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubcategoryResponseDTO>> getById(@PathVariable Long id){
        return ResponseEntity.ok(new ApiResponse<>("Subcategoria recuperada com sucesso", service.toResponse(service.findById(id))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubcategoryResponseDTO>> create(@RequestBody @Valid SubcategoryCreateDTO dto){
        return ResponseEntity.ok(new ApiResponse<>("Subcategoria criada com sucesso", service.toResponse(service.create(dto))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubcategoryResponseDTO>> update(@PathVariable Long id, @RequestBody @Valid SubcategoryCreateDTO dto){
        return ResponseEntity.ok(new ApiResponse<>("Subcategoria atualizada com sucesso", service.toResponse(service.update(id, dto))));
    }

    /* Exclusão lógica: a subcategoria é desativada, não apagada */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<SubcategoryResponseDTO>> deactivate(@PathVariable Long id){
        return ResponseEntity.ok(new ApiResponse<>("Subcategoria desativada com sucesso", service.toResponse(service.deactivate(id))));
    }

    @PutMapping("/{id}/reactivate")
    public ResponseEntity<ApiResponse<SubcategoryResponseDTO>> reactivate(@PathVariable Long id){
        return ResponseEntity.ok(new ApiResponse<>("Subcategoria reativada com sucesso", service.toResponse(service.reactivate(id))));
    }
}
