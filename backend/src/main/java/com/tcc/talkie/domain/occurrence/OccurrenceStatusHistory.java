package com.tcc.talkie.domain.occurrence;

import java.time.LocalDateTime;

import com.tcc.talkie.domain.user.User;

import jakarta.persistence.*;
import lombok.*;

/* Evento de mudança de status. changedBy fica nulo quando o autor do evento exclui a conta. */
@Entity
@Table(name = "occurrence_status_history")
@Getter
@Setter
@NoArgsConstructor
public class OccurrenceStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "occurrence_id", nullable = false)
    private Occurrence occurrence;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private OccurrenceStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OccurrenceStatus newStatus;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @ManyToOne
    @JoinColumn(name = "changed_by")
    private User changedBy;

    @Column(nullable = false)
    private LocalDateTime changedAt;
}
