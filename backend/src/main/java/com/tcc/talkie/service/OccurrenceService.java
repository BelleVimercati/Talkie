package com.tcc.talkie.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.category.Subcategory;
import com.tcc.talkie.domain.occurrence.Occurrence;
import com.tcc.talkie.domain.occurrence.OccurrenceStatus;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.request.OccurrenceDTO;
import com.tcc.talkie.dto.response.OccurrenceResponseDTO;
import com.tcc.talkie.events.OccurrenceCreatedEvent;
import com.tcc.talkie.infra.exceptions.NotFoundException;
import com.tcc.talkie.infra.security.AuthenticatedUser;
import com.tcc.talkie.repository.CategoryRepository;
import com.tcc.talkie.repository.OccurrenceRepository;
import com.tcc.talkie.repository.SubcategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OccurrenceService {

    private final OccurrenceRepository repository;
    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Occurrence create(OccurrenceDTO dto){
        Category category = categoryRepository.findById(dto.categoryId()).orElseThrow(() -> new NotFoundException("Categoria não encontrada"));

        Subcategory subcategory = subcategoryRepository.findById(dto.subcategoryId()).orElseThrow(() -> new NotFoundException("Subcategoria não encontrada"));

        User user = AuthenticatedUser.get();

        Occurrence occurrence = new Occurrence();

        occurrence.setTitle(dto.title());
        occurrence.setDescription(dto.description());
        occurrence.setLocation(dto.location());

        occurrence.setCategory(category);
        occurrence.setSubcategory(subcategory);
        occurrence.setOwner(user);

        occurrence.setCreatedAt(LocalDateTime.now());

        Occurrence savedOccurrence = repository.save(occurrence);
        eventPublisher.publishEvent(new OccurrenceCreatedEvent(savedOccurrence));
        return savedOccurrence;
    }

    public OccurrenceResponseDTO updateStatus(Long id, OccurrenceStatus status) {
        Occurrence occurrence = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Ocorrência não encontrada"));
        occurrence.setStatus(status);
        occurrence.setResolvedAt(status == OccurrenceStatus.RESOLVIDO ? LocalDateTime.now() : null);
        Occurrence updated = repository.save(occurrence);
        return mapToResponse(updated);
    }

    public List<OccurrenceResponseDTO> findByCategory(Long categoryId) {
        return repository.findByCategoryId(categoryId)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    public List<OccurrenceResponseDTO> findAll(){
        return repository.findAll()
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    public void delete(Long id){
        if (repository.findById(id).isEmpty()) {
            throw new NotFoundException("Ocorrência não encontrada");
        }
        repository.deleteById(id);
    }


    public List<OccurrenceResponseDTO> findByLoggedUser(){

        UUID userId = AuthenticatedUser.getId();

        log.info("Buscando ocorrências do usuário com ID: {}", userId);

        List<Occurrence> occurrences = repository.findByOwnerId(userId);

        return occurrences.stream()
            .map(this::mapToResponse)
            .toList();
    }

    private OccurrenceResponseDTO mapToResponse(Occurrence o) {
        return new OccurrenceResponseDTO(
            o.getId(),
            o.getTitle(),
            o.getDescription(),
            o.getLocation(),
            o.getOwner().getId(),
            o.getOwner().getName(),
            o.getCategory().getName(),
            o.getSubcategory().getName(),
            o.getStatus(),
            o.getCreatedAt(),
            o.getResolvedAt()
        );
    }

}
