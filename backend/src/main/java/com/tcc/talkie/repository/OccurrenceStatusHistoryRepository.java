package com.tcc.talkie.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tcc.talkie.domain.occurrence.OccurrenceStatusHistory;

public interface OccurrenceStatusHistoryRepository extends JpaRepository<OccurrenceStatusHistory, Long> {

    List<OccurrenceStatusHistory> findByOccurrenceIdOrderByChangedAtAscIdAsc(Long occurrenceId);

    @Modifying
    @Query("update OccurrenceStatusHistory h set h.changedBy = null where h.changedBy.id = :userId")
    int detachActor(@Param("userId") UUID userId);

    @Modifying
    @Query("delete from OccurrenceStatusHistory h where h.occurrence.id = :occurrenceId")
    void deleteByOccurrenceId(@Param("occurrenceId") Long occurrenceId);
}
