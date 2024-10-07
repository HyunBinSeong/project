package com.project.my.dto;

import com.project.my.model.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public class UserDTO {
        private String id;
        private String username;
        private String email;
        private String picture;
        private String accessToken;
        private String refreshToken;

        public UserDTO(final UserEntity entity) {
            this.id = entity.getId();
            this.username = entity.getUsername();
            this.email = entity.getEmail();
            this.picture = entity.getPicture();
        }

        public static UserEntity toEntity(final UserDTO dto) {
            return UserEntity.builder()
                    .id(dto.getId())
                    .username(dto.getUsername())
                    .email(dto.getEmail())
                    .picture(dto.getPicture())
                    .build();
        }

    }
