package com.pines.flutter.capacitacion.api.controller;

import com.pines.flutter.capacitacion.api.dto.PokemonDTO;
import com.pines.flutter.capacitacion.api.mapper.PokemonMapper;
import com.pines.flutter.capacitacion.api.model.pokemon.Pokemon;
import com.pines.flutter.capacitacion.api.repository.PokemonRepository;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Get all Pokemon", description = "Retrieve all Pokemon ordered by name")
    public ResponseEntity<List<PokemonDTO>> getAllPokemon() {
        List<Pokemon> pokemon = pokemonRepository.findAll();
        List<PokemonDTO> pokemonDtos = pokemon.stream()
                .map(pokemonMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pokemonDtos);
    }
}
