package com.project.my.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;


import java.util.ArrayList;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "picture", nullable = true, unique = false)
    private String picture;


    public static UserEntity createUser(String username, String email, String picture) {
        return UserEntity.builder()
                .username(username)
                .email(email)
                .picture(picture)
                .build();
    }
}