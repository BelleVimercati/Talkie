package com.tcc.talkie.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.category.Subcategory;
import com.tcc.talkie.domain.occurrence.Occurrence;
import com.tcc.talkie.domain.occurrence.OccurrenceStatus;
import com.tcc.talkie.domain.occurrence.OccurrenceStatusHistory;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.request.OccurrenceDTO;
import com.tcc.talkie.dto.response.OccurrenceResponseDTO;
import com.tcc.talkie.dto.response.OccurrenceStatusHistoryResponseDTO;
import com.tcc.talkie.events.OccurrenceCreatedEvent;
import com.tcc.talkie.infra.exceptions.BadRequestException;
import com.tcc.talkie.infra.exceptions.NotFoundException;
import com.tcc.talkie.infra.security.AuthenticatedUser;
import com.tcc.talkie.repository.CategoryRepository;
import com.tcc.talkie.repository.OccurrenceRepository;
import com.tcc.talkie.repository.OccurrenceStatusHistoryRepository;
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
    private final OccurrenceStatusHistoryRepository historyRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Occurrence create(OccurrenceDTO dto){
        validateCoordinates(dto.latitude(), dto.longitude());

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
        occurrence.setOccurredAt(dto.occurredAt() != null ? dto.occurredAt() : LocalDateTime.now());
        occurrence.setLatitude(dto.latitude());
        occurrence.setLongitude(dto.longitude());

        Occurrence savedOccurrence = repository.save(occurrence);
        recordStatusChange(savedOccurrence, null, savedOccurrence.getStatus(), null, user);
        eventPublisher.publishEvent(new OccurrenceCreatedEvent(savedOccurrence));
        return savedOccurrence;
    }

    @Transactional
    public OccurrenceResponseDTO updateStatus(Long id, OccurrenceStatus status, String reason) {
        Occurrence occurrence = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Ocorrência não encontrada"));
        OccurrenceStatus previous = occurrence.getStatus();
        occurrence.setStatus(status);
        occurrence.setResolvedAt(status == OccurrenceStatus.RESOLVIDO ? LocalDateTime.now() : null);
        Occurrence updated = repository.save(occurrence);
        String trimmedReason = reason != null && !reason.isBlank() ? reason.trim() : null;
        recordStatusChange(updated, previous, status, trimmedReason, AuthenticatedUser.get());
        return mapToResponse(updated);
    }

    public List<OccurrenceStatusHistoryResponseDTO> findHistory(Long occurrenceId) {
        if (repository.findById(occurrenceId).isEmpty()) {
            throw new NotFoundException("Ocorrência não encontrada");
        }
        return historyRepository.findByOccurrenceIdOrderByChangedAtAscIdAsc(occurrenceId)
            .stream()
            .map(h -> new OccurrenceStatusHistoryResponseDTO(
                h.getId(),
                h.getPreviousStatus(),
                h.getNewStatus(),
                h.getReason(),
                h.getChangedAt(),
                h.getChangedBy() != null ? h.getChangedBy().getId() : null,
                h.getChangedBy() != null ? h.getChangedBy().getName() : null
            ))
            .toList();
    }

    /* @DecimalMin/@DecimalMax do DTO não rodam sem spring-boot-starter-validation */
    private void validateCoordinates(Double latitude, Double longitude) {
        if ((latitude == null) != (longitude == null)) {
            throw new BadRequestException("Latitude e longitude devem ser informadas juntas");
        }
        if (latitude == null) return;
        if (latitude < -90 || latitude > 90) {
            throw new BadRequestException("Latitude deve estar entre -90 e 90");
        }
        if (longitude < -180 || longitude > 180) {
            throw new BadRequestException("Longitude deve estar entre -180 e 180");
        }
    }

    private void recordStatusChange(Occurrence occurrence, OccurrenceStatus previous, OccurrenceStatus next, String reason, User actor) {
        OccurrenceStatusHistory entry = new OccurrenceStatusHistory();
        entry.setOccurrence(occurrence);
        entry.setPreviousStatus(previous);
        entry.setNewStatus(next);
        entry.setReason(reason);
        entry.setChangedBy(actor);
        entry.setChangedAt(LocalDateTime.now());
        historyRepository.save(entry);
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

    @Transactional
    public void delete(Long id){
        if (repository.findById(id).isEmpty()) {
            throw new NotFoundException("Ocorrência não encontrada");
        }
        historyRepository.deleteByOccurrenceId(id);
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

    public OccurrenceResponseDTO mapToResponse(Occurrence o) {
        return new OccurrenceResponseDTO(
            o.getId(),
            o.getTitle(),
            o.getDescription(),
            o.getLocation(),
            o.getOwner() != null ? o.getOwner().getId() : null,
            o.getOwner() != null ? o.getOwner().getName() : null,
            o.getCategory().getName(),
            o.getSubcategory().getName(),
            o.getStatus(),
            o.getCreatedAt(),
            o.getResolvedAt(),
            o.getOccurredAt(),
            o.getLatitude(),
            o.getLongitude(),
            o.getImagePath() != null ? "/occurrences/" + o.getId() + "/image" : null
        );
    }

}
