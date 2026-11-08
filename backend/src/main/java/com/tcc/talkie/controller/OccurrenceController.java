package com.tcc.talkie.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import com.tcc.talkie.domain.occurrence.Occurrence;
import com.tcc.talkie.dto.response.ApiResponse;
import com.tcc.talkie.dto.ErrorResponse;
import com.tcc.talkie.dto.request.OccurrenceDTO;
import com.tcc.talkie.dto.request.OccurrenceStatusUpdateDTO;
import com.tcc.talkie.dto.response.OccurrenceResponseDTO;
import com.tcc.talkie.dto.response.OccurrenceStatusHistoryResponseDTO;
import com.tcc.talkie.service.OccurrenceService;
import com.tcc.talkie.service.StorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/occurrences")
@RequiredArgsConstructor
public class OccurrenceController {
    
    private final OccurrenceService service;
    private final StorageService storageService;

    @PostMapping
    public ResponseEntity<ApiResponse<OccurrenceResponseDTO>> create(@Valid @RequestBody OccurrenceDTO data){
        Occurrence created = service.create(data);
        OccurrenceResponseDTO response = service.mapToResponse(created);
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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OccurrenceResponseDTO>> getById(@PathVariable Long id){
        return ResponseEntity.ok(new ApiResponse<>("Ocorrência recuperada com sucesso", service.findById(id)));
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<OccurrenceResponseDTO>> uploadImage(
        @PathVariable Long id,
        @RequestParam("file") MultipartFile file
    ){
        return ResponseEntity.ok(new ApiResponse<>("Imagem enviada com sucesso", service.attachImage(id, file)));
    }

    /* Bytes da imagem (exige o mesmo acesso do detalhe da ocorrência) */
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id){
        Occurrence occurrence = service.findWithImage(id);
        String filename = occurrence.getImagePath();
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(storageService.contentType(filename)))
            .cacheControl(CacheControl.noStore().cachePrivate())
            .body(storageService.load(filename));
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
        OccurrenceResponseDTO result = service.updateStatus(id, dto.status(), dto.reason());
        return ResponseEntity.ok(new ApiResponse<>("Status atualizado com sucesso", result));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<List<OccurrenceStatusHistoryResponseDTO>>> getHistory(@PathVariable Long id){
        List<OccurrenceStatusHistoryResponseDTO> history = service.findHistory(id);
        return ResponseEntity.ok(new ApiResponse<>("Histórico recuperado com sucesso", history));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.ok(new ErrorResponse("Ocorrência deletada com sucesso", 200, LocalDate.now().toString()));
    }
}
