package com.pines.flutter.capacitacion.api.controller;

import com.pines.flutter.capacitacion.api.dto.PokemonDTO;
import com.pines.flutter.capacitacion.api.mapper.PokemonMapper;
import com.pines.flutter.capacitacion.api.model.pokemon.Pokemon;
import com.pines.flutter.capacitacion.api.model.pokemon.PokemonType;
import com.pines.flutter.capacitacion.api.repository.PokemonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PokemonController Unit Tests")
class PokemonControllerTest {

    @Mock
    private PokemonRepository pokemonRepository;

    @Mock
    private PokemonMapper pokemonMapper;

    @InjectMocks
    private PokemonController pokemonController;

    private PokemonType fireType;
    private PokemonType waterType;
    private Pokemon charmander;
    private Pokemon squirtle;
    private List<Pokemon> pokemonList;
    private PokemonDTO charmanderDto;
    private PokemonDTO squirtleDto;

    @BeforeEach
    void setUp() {
        // Setup PokemonType test data
        fireType = new PokemonType();
        fireType.setId(1L);
        fireType.setName(PokemonType.TypeName.FIRE);
        fireType.setLightColor("#F08030");
        fireType.setDarkColor("#D06010");

        waterType = new PokemonType();
        waterType.setId(2L);
        waterType.setName(PokemonType.TypeName.WATER);
        waterType.setLightColor("#6890F0");
        waterType.setDarkColor("#4870D0");

        // Setup Pokemon test data
        charmander = new Pokemon();
        charmander.setId(1L);
        charmander.setName("charmander");
        charmander.setPicture("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/4.png");
        charmander.setShinyPicture("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/shiny/4.png");
        charmander.setType(fireType);

        squirtle = new Pokemon();
        squirtle.setId(2L);
        squirtle.setName("squirtle");
        squirtle.setPicture("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/7.png");
        squirtle.setShinyPicture("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/shiny/7.png");
        squirtle.setType(waterType);

        pokemonList = Arrays.asList(charmander, squirtle);

        // Setup DTO test data
        charmanderDto = new PokemonDTO(
                1L,
                "charmander",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/4.png",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/shiny/4.png",
                1L
        );

        squirtleDto = new PokemonDTO(
                2L,
                "squirtle",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/7.png",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/shiny/7.png",
                2L
        );

        // Setup mapper mock behavior with lenient stubbing
        lenient().when(pokemonMapper.toDto(charmander)).thenReturn(charmanderDto);
        lenient().when(pokemonMapper.toDto(squirtle)).thenReturn(squirtleDto);
    }

    @Test
    @DisplayName("Should return all Pokemon when repository has data")
    void getAllPokemon_WhenDataExists_ShouldReturnAllPokemon() {
        // Given
        when(pokemonRepository.findAll()).thenReturn(pokemonList);

        // When
        ResponseEntity<List<PokemonDTO>> response = pokemonController.getAllPokemon();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).containsExactlyInAnyOrder(charmanderDto, squirtleDto);
        
        verify(pokemonRepository, times(1)).findAll();
        verify(pokemonMapper, times(1)).toDto(charmander);
        verify(pokemonMapper, times(1)).toDto(squirtle);
    }

    @Test
    @DisplayName("Should return empty list when repository has no data")
    void getAllPokemon_WhenNoData_ShouldReturnEmptyList() {
        // Given
        when(pokemonRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<PokemonDTO>> response = pokemonController.getAllPokemon();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
        
        verify(pokemonRepository, times(1)).findAll();
        verify(pokemonMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should handle repository exception gracefully")
    void getAllPokemon_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Given
        RuntimeException repositoryException = new RuntimeException("Database connection failed");
        when(pokemonRepository.findAll()).thenThrow(repositoryException);

        // When & Then
        try {
            pokemonController.getAllPokemon();
        } catch (RuntimeException e) {
            assertThat(e).isEqualTo(repositoryException);
            assertThat(e.getMessage()).isEqualTo("Database connection failed");
        }
        
        verify(pokemonRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return Pokemon with complete type information")
    void getAllPokemon_ShouldReturnPokemonData() {
        // Given
        when(pokemonRepository.findAll()).thenReturn(pokemonList);

        // When
        ResponseEntity<List<PokemonDTO>> response = pokemonController.getAllPokemon();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        
        List<PokemonDTO> body = response.getBody();
        PokemonDTO returnedCharmander = body.stream()
                .filter(p -> p.getName().equals("charmander"))
                .findFirst()
                .orElse(null);
        
        assertThat(returnedCharmander).isNotNull();
        assertThat(returnedCharmander.getTypeId()).isEqualTo(1L);
        assertThat(returnedCharmander.getPicture()).isEqualTo("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/4.png");
        assertThat(returnedCharmander.getShinyPicture()).isEqualTo("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/shiny/4.png");
        
        verify(pokemonRepository, times(1)).findAll();
        verify(pokemonMapper, times(1)).toDto(charmander);
        verify(pokemonMapper, times(1)).toDto(squirtle);
    }
}
