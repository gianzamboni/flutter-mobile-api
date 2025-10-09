package com.pines.flutter.capacitacion.api.service;

import com.pines.flutter.capacitacion.api.dto.AuthResponseDTO;
import com.pines.flutter.capacitacion.api.dto.UserDTO;
import com.pines.flutter.capacitacion.api.dto.UserLoginDTO;
import com.pines.flutter.capacitacion.api.dto.UserRegistrationDTO;
import com.pines.flutter.capacitacion.api.mapper.UserMapper;
import com.pines.flutter.capacitacion.api.model.user.User;
import com.pines.flutter.capacitacion.api.repository.UserRepository;
import com.pines.flutter.capacitacion.api.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Ash");
        user.setSurname("Ketchum");
        user.setUsername("ash");
        user.setPassword("enc");

        userDto = new UserDTO(1L, "Ash", "Ketchum", "ash");

        lenient().when(userMapper.toDto(user)).thenReturn(userDto);
    }

    @Test
    @DisplayName("registerUser should create user when username available")
    void registerUser_Success() {
        UserRegistrationDTO dto = new UserRegistrationDTO("Ash", "Ketchum", "ash", "pw");

        when(userRepository.existsByUsername("ash")).thenReturn(false);
        when(passwordEncoder.encode("pw")).thenReturn("enc");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO result = userService.registerUser(dto);

        assertThat(result).isEqualTo(userDto);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("registerUser should throw when username exists")
    void registerUser_UsernameExists() {
        UserRegistrationDTO dto = new UserRegistrationDTO("Ash", "Ketchum", "ash", "pw");
        when(userRepository.existsByUsername("ash")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username already exists");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("loginUser should return token and user when credentials valid")
    void loginUser_Success() {
        UserLoginDTO login = new UserLoginDTO("ash", "pw");
        when(userRepository.findByUsername("ash")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pw", "enc")).thenReturn(true);
        when(jwtTokenProvider.generateToken("ash")).thenReturn("token123");

        AuthResponseDTO response = userService.loginUser(login);

        assertThat(response.getToken()).isEqualTo("token123");
        assertThat(response.getUser()).isEqualTo(userDto);
    }

    @Test
    @DisplayName("loginUser should throw BadCredentials when user not found")
    void loginUser_UserNotFound() {
        UserLoginDTO login = new UserLoginDTO("ash", "pw");
        when(userRepository.findByUsername("ash")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loginUser(login))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    @DisplayName("loginUser should throw BadCredentials when password mismatch")
    void loginUser_WrongPassword() {
        UserLoginDTO login = new UserLoginDTO("ash", "wrong");
        when(userRepository.findByUsername("ash")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "enc")).thenReturn(false);

        assertThatThrownBy(() -> userService.loginUser(login))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid username or password");
    }
}


