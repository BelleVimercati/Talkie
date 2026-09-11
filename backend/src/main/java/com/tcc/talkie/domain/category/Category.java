package com.tcc.talkie.domain.category;

import com.tcc.talkie.domain.user.User;

import java.util.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "types")
@Getter
@Setter
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name; 

    private String icon;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subcategory> subcategories = new ArrayList<>();
}
