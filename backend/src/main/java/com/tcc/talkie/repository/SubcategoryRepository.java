package com.tcc.talkie.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tcc.talkie.domain.category.Subcategory;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {
    Optional<Subcategory> findByName(String name);

    Optional<Subcategory> findById(Long id);

    Optional<Subcategory> findByNameIgnoreCaseAndCategoryId(String name, Long categoryId);

    List<Subcategory> findByActiveTrueAndCategoryActiveTrue();
}