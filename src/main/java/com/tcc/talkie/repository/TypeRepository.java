package com.tcc.talkie.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tcc.talkie.domain.type.Type;

public interface TypeRepository extends JpaRepository<Type, Long> {
    Optional<Type> findByName(String name);

    Optional<Type> findById(Long id);
}
