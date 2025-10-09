package com.pines.flutter.capacitacion.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.Principal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HellowUserController Unit Tests")
class HellowUserControllerTest {

    @Test
    @DisplayName("hello should greet principal by name")
    void hello_ReturnsGreeting() {
        HellowUserController controller = new HellowUserController();
        Principal principal = () -> "ash";

        String result = controller.hello(principal);

        assertThat(result).isEqualTo("Hello, ash");
    }
}


