package com.pines.flutter.capacitacion.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.security.Principal;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class HellowUserController {

    @GetMapping("/api/hello")
    public String hello(Principal principal) {
        return "Hello, " + principal.getName();
    }
}
