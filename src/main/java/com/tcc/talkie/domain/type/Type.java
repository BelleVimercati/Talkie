package com.tcc.talkie.domain.type;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "types")
@Getter
@Setter
@NoArgsConstructor
public class Type {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name; 

    private String icon;
}
