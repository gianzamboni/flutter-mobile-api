package com.pines.flutter.capacitacion.api.service;

import com.pines.flutter.capacitacion.api.dto.AuthResponseDTO;
import com.pines.flutter.capacitacion.api.dto.UserDTO;
import com.pines.flutter.capacitacion.api.dto.UserLoginDTO;
import com.pines.flutter.capacitacion.api.dto.UserRegistrationDTO;
import com.pines.flutter.capacitacion.api.model.user.User;
import com.pines.flutter.capacitacion.api.repository.UserRepository;
import com.pines.flutter.capacitacion.api.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pines.flutter.capacitacion.api.mapper.UserMapper;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private final UserMapper userMapper;

    @Transactional
    public UserDTO registerUser(UserRegistrationDTO registrationDTO) {
        // Check if username already exists
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Create new user
        User user = new User();
        user.setName(registrationDTO.getName());
        user.setSurname(registrationDTO.getSurname());
        user.setUsername(registrationDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));

        // Save user
        user = userRepository.save(user);

        // Create response
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO loginUser(UserLoginDTO loginDTO) {
        // Find user by username
        User user = userRepository.findByUsername(loginDTO.getUsername())
            .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        // Verify password
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // Generate token
        String token = jwtTokenProvider.generateToken(user.getUsername());

        // Create response
        return new AuthResponseDTO(
            token,
            user.getId(),
            user.getUsername(),
            user.getName(),
            user.getSurname()
        );
    }
}
