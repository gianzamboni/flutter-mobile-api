package com.pines.flutter.capacitacion.api.controller;

import com.pines.flutter.capacitacion.api.dto.AuthResponseDTO;
import com.pines.flutter.capacitacion.api.dto.UserDTO;
import com.pines.flutter.capacitacion.api.dto.UserLoginDTO;
import com.pines.flutter.capacitacion.api.dto.UserRegistrationDTO;
import com.pines.flutter.capacitacion.api.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Unit Tests")
class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController controller;

    @Test
    @DisplayName("registerUser returns 201 on success")
    void registerUser_Success() {
        UserRegistrationDTO req = new UserRegistrationDTO("Ash", "Ketchum", "ash", "pw");
        UserDTO userDto = new UserDTO(1L, "Ash", "Ketchum", "ash");
        when(userService.registerUser(req)).thenReturn(userDto);

        ResponseEntity<?> response = controller.registerUser(req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(userDto);
    }

    @Test
    @DisplayName("registerUser returns 400 when IllegalArgumentException")
    void registerUser_BadRequest() {
        UserRegistrationDTO req = new UserRegistrationDTO("Ash", "Ketchum", "ash", "pw");
        when(userService.registerUser(req)).thenThrow(new IllegalArgumentException("Username already exists"));

        ResponseEntity<?> response = controller.registerUser(req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Username already exists");
    }

    @Test
    @DisplayName("registerUser returns 500 on generic exception")
    void registerUser_ServerError() {
        UserRegistrationDTO req = new UserRegistrationDTO("Ash", "Ketchum", "ash", "pw");
        when(userService.registerUser(req)).thenThrow(new RuntimeException("boom"));

        ResponseEntity<?> response = controller.registerUser(req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("An error occurred during registration");
    }

    @Test
    @DisplayName("loginUser returns 200 on success")
    void loginUser_Success() {
        UserLoginDTO req = new UserLoginDTO("ash", "pw");
        UserDTO userDto = new UserDTO(1L, "Ash", "Ketchum", "ash");
        AuthResponseDTO auth = new AuthResponseDTO("token123", userDto);
        when(userService.loginUser(req)).thenReturn(auth);

        ResponseEntity<?> response = controller.loginUser(req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(auth);
    }

    @Test
    @DisplayName("loginUser returns 401 on exception")
    void loginUser_Unauthorized() {
        UserLoginDTO req = new UserLoginDTO("ash", "wrong");
        when(userService.loginUser(req)).thenThrow(new RuntimeException("Invalid username or password"));

        ResponseEntity<?> response = controller.loginUser(req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo("Invalid username or password");
    }
}


