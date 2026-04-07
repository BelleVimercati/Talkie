package com.tcc.talkie.domain.user;

import java.util.UUID;

import com.tcc.talkie.domain.user.Role;

import com.tcc.talkie.infra.security.crypto.CryptoConverter;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    @Convert(converter = CryptoConverter.class)
    private String cpf;

    @Column(unique = true)
    private String cpfHash;

    @Enumerated(EnumType.STRING)
    private Role role;
}
