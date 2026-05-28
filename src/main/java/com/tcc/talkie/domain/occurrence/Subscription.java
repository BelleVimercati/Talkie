package com.tcc.talkie.domain.occurrence;

import java.time.LocalDateTime;

import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.user.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscriptions",
    uniqueConstraints = @UniqueConstraint(columnNames = {"subscriber_id", "category_id"})
)
@Getter
@Setter
@NoArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User subscriber;

    @ManyToOne
    private Category category;

    private LocalDateTime subscribedAt = LocalDateTime.now();
}
