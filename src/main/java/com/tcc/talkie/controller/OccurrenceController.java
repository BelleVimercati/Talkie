package com.tcc.talkie.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.talkie.domain.occurrence.Occurrence;
import com.tcc.talkie.dto.request.OccurrenceDTO;
import com.tcc.talkie.dto.response.OccurrenceResponseDTO;
import com.tcc.talkie.service.OccurrenceService;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/* A fazer:
    - Entender com o Bazilio se existe a necessidade de criar a exclusão de ocorrências */
@Slf4j
@RestController
@RequestMapping("/occurrences")
@RequiredArgsConstructor
public class OccurrenceController {
    
    private final OccurrenceService service;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody OccurrenceDTO data){
        Occurrence created = service.create(data);
        return ResponseEntity.ok(new OccurrenceResponseDTO(created.getTitle(), created.getDescription(), created.getLocation(), created.getOwner().getId(), created.getCategory().getId(), created.getSubcategory().getId()));
    }

    @GetMapping
    public ResponseEntity<?> getAll(){
        List<OccurrenceResponseDTO> occurrences = service.findAll()
        .stream()
        .map(occ -> new OccurrenceResponseDTO(
            occ.getTitle(),
            occ.getDescription(),
            occ.getLocation(),
            occ.getOwner().getId(),
            occ.getCategory().getId(),
            occ.getSubcategory().getId()
        ))
        .toList();
        return ResponseEntity.ok(occurrences);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getByUser(){
        List<OccurrenceResponseDTO> occurrences = service.findByLoggedUser()
        .stream()
        .map(occ -> new OccurrenceResponseDTO(
            occ.title(),
            occ.description(),
            occ.location(),
            occ.ownerId(),
            occ.category(),
            occ.subcategory()
        ))
        .toList();

        log.info("Ocorrências encontradas para o usuário: {}", occurrences.size());
        return ResponseEntity.ok(occurrences);
    }
}
