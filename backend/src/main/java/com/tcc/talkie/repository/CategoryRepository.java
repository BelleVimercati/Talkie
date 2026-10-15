package com.tcc.talkie.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tcc.talkie.domain.category.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameIgnoreCase(String name);

    Optional<Category> findById(Long id);

    @Modifying
    @Query("update Category c set c.user = null where c.user.id = :userId")
    int detachCreator(@Param("userId") UUID userId);
}
