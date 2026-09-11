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

import jakarta.validation.Valid;

import com.tcc.talkie.domain.occurrence.Occurrence;
import com.tcc.talkie.dto.response.ApiResponse;
import com.tcc.talkie.dto.ErrorResponse;
import com.tcc.talkie.dto.request.OccurrenceDTO;
import com.tcc.talkie.dto.request.OccurrenceStatusUpdateDTO;
import com.tcc.talkie.dto.response.OccurrenceResponseDTO;
import com.tcc.talkie.service.OccurrenceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/occurrences")
@RequiredArgsConstructor
public class OccurrenceController {
    
    private final OccurrenceService service;

    @PostMapping
    public ResponseEntity<ApiResponse<OccurrenceResponseDTO>> create(@Valid @RequestBody OccurrenceDTO data){
        Occurrence created = service.create(data);
        OccurrenceResponseDTO response = new OccurrenceResponseDTO(
            created.getTitle(),
            created.getDescription(),
            created.getLocation(),
            created.getOwner().getId(),
            created.getCategory().getName(),
            created.getSubcategory().getName(),
            created.getStatus()
        );
        return ResponseEntity.ok(new ApiResponse<>("Ocorrência criada com sucesso", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OccurrenceResponseDTO>>> getAll(){
        List<OccurrenceResponseDTO> occurrences = service.findAll();
        return ResponseEntity.ok(new ApiResponse<>("Ocorrências recuperadas com sucesso", occurrences));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<OccurrenceResponseDTO>>> getByUser(){
        List<OccurrenceResponseDTO> occurrences = service.findByLoggedUser();

        log.info("Ocorrências encontradas para o usuário: {}", occurrences.size());
        return ResponseEntity.ok(new ApiResponse<>("Suas ocorrências recuperadas com sucesso", occurrences));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<OccurrenceResponseDTO>>> getByCategory(@PathVariable Long categoryId){
        List<OccurrenceResponseDTO> occurrences = service.findByCategory(categoryId);
        return ResponseEntity.ok(new ApiResponse<>("Ocorrências da categoria recuperadas com sucesso", occurrences));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OccurrenceResponseDTO>> updateStatus(
        @PathVariable Long id,
        @Valid @RequestBody OccurrenceStatusUpdateDTO dto
    ){
        OccurrenceResponseDTO result = service.updateStatus(id, dto.status());
        return ResponseEntity.ok(new ApiResponse<>("Status atualizado com sucesso", result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.ok(new ErrorResponse("Ocorrência deletada com sucesso", 200, LocalDate.now().toString()));
    }
}
