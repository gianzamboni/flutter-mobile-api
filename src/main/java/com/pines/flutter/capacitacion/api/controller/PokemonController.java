package com.pines.flutter.capacitacion.api.controller;

import com.pines.flutter.capacitacion.api.dto.PokemonDTO;
import com.pines.flutter.capacitacion.api.mapper.PokemonMapper;
import com.pines.flutter.capacitacion.api.model.pokemon.Pokemon;
import com.pines.flutter.capacitacion.api.repository.PokemonRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pokemon")
@AllArgsConstructor
@Tag(name = "Pokemon")
public class PokemonController {

    private final PokemonRepository pokemonRepository;
    private final PokemonMapper pokemonMapper;

    @GetMapping
    @Operation(summary = "Get Pokemon by IDs", description = "Retrieve Pokemon by a list of IDs. If no IDs provided, returns all Pokemon")
    public ResponseEntity<List<PokemonDTO>> getAllPokemon(
            @Parameter(description = "List of Pokemon IDs to filter by. If not provided or empty, returns all Pokemon", 
                      example = "1,2,3")
            @RequestParam(required = false) List<Long> ids) {
        List<Pokemon> pokemon;
        
        if (ids == null || ids.isEmpty()) {
            pokemon = pokemonRepository.findAll();
        } else {
            pokemon = pokemonRepository.findAllById(ids);
        }
        
        List<PokemonDTO> pokemonDtos = pokemon.stream()
                .map(pokemonMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pokemonDtos);
    }
}
