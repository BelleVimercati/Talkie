package com.tcc.talkie.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.category.Subcategory;
import com.tcc.talkie.domain.occurrence.Occurrence;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.request.OccurrenceDTO;
import com.tcc.talkie.dto.response.OccurrenceResponseDTO;
import com.tcc.talkie.infra.exceptions.NotFoundException;
import com.tcc.talkie.infra.security.AuthenticatedUser;
import com.tcc.talkie.repository.CategoryRepository;
import com.tcc.talkie.repository.OccurrenceRepository;
import com.tcc.talkie.repository.SubcategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/* A fazer:
    - cadastro de ocorrencias - ok
    - listar todas as ocorrencias - ok
    - listar ocorrencias por usuario - ok
    - excluir ocorrencias - ok
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class OccurrenceService {

    private final OccurrenceRepository repository;
    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;

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

        return repository.save(occurrence);
    }

    public List<Occurrence> findAll(){
        return repository.findAll();
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

        List<Occurrence> occurrences =
            repository.findByOwnerId(userId);

        return occurrences.stream()
            .map(o -> new OccurrenceResponseDTO(
                o.getTitle(),
                o.getDescription(),
                o.getLocation(),
                o.getOwner().getId(),
                o.getCategory().getId(),
                o.getSubcategory().getId()
            ))
            .toList();
    }

}
