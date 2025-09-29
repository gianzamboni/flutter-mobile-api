package com.pines.flutter.capacitacion.api.controller;

import com.pines.flutter.capacitacion.api.model.pokemon.PokemonType;
import com.pines.flutter.capacitacion.api.repository.PokemonTypeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pokemon-types")
@AllArgsConstructor
@Tag(name = "Pokemon Types")
public class PokemonTypeController {

    private final PokemonTypeRepository pokemonTypeRepository;

    @GetMapping
    @Operation(summary = "Get all Pokemon types with colors", description = "Retrieve all Pokemon types with their light and dark colors")
    public ResponseEntity<List<PokemonType>> getAllPokemonTypes() {
        List<PokemonType> pokemonTypes = pokemonTypeRepository.findAll();
        return ResponseEntity.ok(pokemonTypes);
    }
}
