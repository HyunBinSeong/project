package com.project.my.service.impl;

import com.project.my.dto.UserDTO;
import com.project.my.model.UserEntity;
import com.project.my.persistence.UserRepository;
import com.project.my.service.UserService;
import com.project.my.util.JwtUtil;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;


    public UserServiceImpl(JwtUtil jwtUtil,UserRepository userRepository) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserDTO saveOrUpdate(UserDTO userDTO) {
        UserEntity userEntity = UserDTO.toEntity(userDTO);
        UserEntity savedEntity = userRepository.save(userEntity);
        return new UserDTO(savedEntity);
    }

    @Override
    public Optional<UserDTO> findByEmail(String email) {
        return userRepository.findByEmail(email).map(UserDTO::new);
    }

    @Override
    public boolean validateAcessToken(String token, UserDTO userDTO) {
        return jwtUtil.validateAccessToken(token, userDTO);
    }

    @Override
    public boolean validateRefreshToken(String token, UserDTO userDTO) {
        return  jwtUtil.validateRefreshToken(token);
    }


}
