package com.pines.flutter.capacitacion.api.controller;

import com.pines.flutter.capacitacion.api.dto.PokemonDTO;
import com.pines.flutter.capacitacion.api.service.PokemonService;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pokemon")
@AllArgsConstructor
@Tag(name = "Pokemon")
public class PokemonController {

    private final PokemonService pokemonService;

    @GetMapping
    @Operation(summary = "Get Pokemon by IDs", description = "Retrieve Pokemon by a list of IDs. If no IDs provided, returns all Pokemon")
    public ResponseEntity<List<PokemonDTO>> getAllPokemon(
            @Parameter(description = "List of Pokemon IDs to filter by. If not provided or empty, returns all Pokemon", 
                      example = "1,2,3")
            @RequestParam(required = false) List<Long> ids) {
        List<PokemonDTO> pokemonDtos = pokemonService.getAllPokemon(ids);
        return ResponseEntity.ok(pokemonDtos);
    }

    @GetMapping(params = "fields")
    @Operation(
            summary = "Get Pokemon with selected fields",
            description = "Optionally select which fields to include. 'id' is always included. Valid fields: id, name, picture, shinyPicture, type"
    )
    public ResponseEntity<MappingJacksonValue> getAllPokemonWithSelectedFields(
            @Parameter(description = "List of Pokemon IDs to filter by.", example = "1,2,3")
            @RequestParam(required = false) List<Long> ids,
            @Parameter(description = "Comma-separated list of fields to include (e.g., name,picture). 'id' is always included.", example = "name")
            @RequestParam String fields) {

        List<PokemonDTO> pokemonDtos = pokemonService.getAllPokemon(ids);

        Set<String> requestedFields = List.of(fields.split(","))
                .stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        // Always include 'id'
        requestedFields.add("id");

        SimpleFilterProvider filters = new SimpleFilterProvider()
                .addFilter("PokemonDTOFilter", SimpleBeanPropertyFilter.filterOutAllExcept(requestedFields));

        MappingJacksonValue wrapper = new MappingJacksonValue(pokemonDtos);
        wrapper.setFilters(filters);

        return ResponseEntity.ok(wrapper);
    }
}
