package com.pines.flutter.capacitacion.api.controller;

import com.pines.flutter.capacitacion.api.dto.AddFavouriteRequestDTO;
import com.pines.flutter.capacitacion.api.dto.PokemonDTO;
import com.pines.flutter.capacitacion.api.dto.SwapFavouritesRequestDTO;
import com.pines.flutter.capacitacion.api.service.FavouritePokemonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/favourite-pokemon")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")   
@Tag(name = "Favourite Pokemon")
public class FavouritePokemonController {

    private final FavouritePokemonService favouritePokemonService;

    @GetMapping
    @Operation(summary = "Get current user's favourite Pokemon")
    public ResponseEntity<List<PokemonDTO>> getFavourites() {
        List<PokemonDTO> favourites = favouritePokemonService.getFavouritePokemonForCurrentUser();
        return ResponseEntity.ok(favourites);
    }

    @PostMapping
    @Operation(summary = "Add a Pokemon to current user's favourites")
    public ResponseEntity<PokemonDTO> addFavourite(@RequestBody AddFavouriteRequestDTO request) {
        PokemonDTO added = favouritePokemonService.addFavouritePokemonForCurrentUser(request.getPokemonId());
        return ResponseEntity.status(HttpStatus.CREATED).body(added);
    }

    @DeleteMapping("/{pokemonId}")
    @Operation(summary = "Remove a Pokemon from current user's favourites")
    public ResponseEntity<Void> removeFavourite(@PathVariable Long pokemonId) {
        favouritePokemonService.removeFavouritePokemonForCurrentUser(pokemonId);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping
    @Operation(summary = "Swap two Pokemon in current user's favourites")
    public ResponseEntity<Void> swapFavourites(@RequestBody SwapFavouritesRequestDTO request) {
        favouritePokemonService.swapFavouritesForCurrentUser(request.getPokemonId1(), request.getPokemonId2());
        return ResponseEntity.noContent().build();
    }
}      

