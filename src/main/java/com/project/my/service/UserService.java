package com.project.my.service;

import com.project.my.dto.UserDTO;
import java.util.Optional;

public interface UserService {
    UserDTO saveOrUpdate(UserDTO userDTO);
    Optional<UserDTO> findByEmail(String email);
    boolean validateAcessToken(String token, UserDTO userDTO);
    boolean validateRefreshToken(String token, UserDTO userDTO);
}
