package com.pines.flutter.capacitacion.api.controller;

import com.pines.flutter.capacitacion.api.dto.PokemonDTO;
import com.pines.flutter.capacitacion.api.service.FavouritePokemonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/favourite-pokemon")
@RequiredArgsConstructor
@Tag(name = "Favourite Pokemon")
public class FavouritePokemonController {

    private final FavouritePokemonService favouritePokemonService;

    @GetMapping
    @Operation(summary = "Get current user's favourite Pokemon")
    public ResponseEntity<List<PokemonDTO>> getFavourites() {
        List<PokemonDTO> favourites = favouritePokemonService.getFavouritePokemonForCurrentUser();
        return ResponseEntity.ok(favourites);
    }
}


