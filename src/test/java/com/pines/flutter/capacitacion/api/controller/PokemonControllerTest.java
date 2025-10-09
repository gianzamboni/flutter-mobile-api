package com.pines.flutter.capacitacion.api.controller;

import com.pines.flutter.capacitacion.api.dto.PokemonDTO;
import com.pines.flutter.capacitacion.api.dto.TypeDTO;
import com.pines.flutter.capacitacion.api.service.PokemonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PokemonController Unit Tests")
class PokemonControllerTest {

    @Mock
    private PokemonService pokemonService;

    @InjectMocks
    private PokemonController pokemonController;

    private TypeDTO fireTypeDto;
    private TypeDTO waterTypeDto;
    private PokemonDTO charmanderDto;
    private PokemonDTO squirtleDto;
    private List<PokemonDTO> pokemonDtoList;

    @BeforeEach
    void setUp() {
        // Setup DTO test data
        fireTypeDto = new TypeDTO(1L, "FIRE");
        waterTypeDto = new TypeDTO(2L, "WATER");
        
        charmanderDto = new PokemonDTO(
                1L,
                "charmander",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/4.png",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/shiny/4.png",
                fireTypeDto
        );

        squirtleDto = new PokemonDTO(
                2L,
                "squirtle",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/7.png",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/shiny/7.png",
                waterTypeDto
        );

        pokemonDtoList = Arrays.asList(charmanderDto, squirtleDto);

        // Lenient default stub if needed
        lenient().when(pokemonService.getAllPokemon(anyList())).thenReturn(pokemonDtoList);
    }

    @Test
    @DisplayName("Should return all Pokemon when no IDs provided and repository has data")
    void getAllPokemon_WhenNoIdsProvidedAndDataExists_ShouldReturnAllPokemon() {
        // Given
        when(pokemonService.getAllPokemon(null)).thenReturn(pokemonDtoList);

        // When
        ResponseEntity<List<PokemonDTO>> response = pokemonController.getAllPokemon(null);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).containsExactlyInAnyOrder(charmanderDto, squirtleDto);

        verify(pokemonService, times(1)).getAllPokemon(ArgumentMatchers.<List<Long>>isNull());
    }

    @Test
    @DisplayName("Should return all Pokemon when empty IDs list provided and repository has data")
    void getAllPokemon_WhenEmptyIdsListProvidedAndDataExists_ShouldReturnAllPokemon() {
        // Given
        when(pokemonService.getAllPokemon(Collections.emptyList())).thenReturn(pokemonDtoList);

        // When
        ResponseEntity<List<PokemonDTO>> response = pokemonController.getAllPokemon(Collections.emptyList());

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).containsExactlyInAnyOrder(charmanderDto, squirtleDto);

        verify(pokemonService, times(1)).getAllPokemon(argThat(list -> list != null && list.isEmpty()));
    }

    @Test
    @DisplayName("Should return specific Pokemon when IDs provided and repository has data")
    void getAllPokemon_WhenIdsProvidedAndDataExists_ShouldReturnSpecificPokemon() {
        // Given
        List<Long> requestedIds = Arrays.asList(1L, 2L);
        when(pokemonService.getAllPokemon(requestedIds)).thenReturn(pokemonDtoList);

        // When
        ResponseEntity<List<PokemonDTO>> response = pokemonController.getAllPokemon(requestedIds);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).containsExactlyInAnyOrder(charmanderDto, squirtleDto);

        verify(pokemonService, times(1)).getAllPokemon(requestedIds);
    }

    @Test
    @DisplayName("Should return single Pokemon when single ID provided")
    void getAllPokemon_WhenSingleIdProvided_ShouldReturnSinglePokemon() {
        // Given
        List<Long> requestedIds = Arrays.asList(1L);
        List<PokemonDTO> singlePokemonDtoList = Arrays.asList(charmanderDto);
        when(pokemonService.getAllPokemon(requestedIds)).thenReturn(singlePokemonDtoList);

        // When
        ResponseEntity<List<PokemonDTO>> response = pokemonController.getAllPokemon(requestedIds);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()).containsExactly(charmanderDto);

        verify(pokemonService, times(1)).getAllPokemon(requestedIds);
    }

    @Test
    @DisplayName("Should return empty list when IDs provided but no matching Pokemon found")
    void getAllPokemon_WhenIdsProvidedButNoMatchingPokemon_ShouldReturnEmptyList() {
        // Given
        List<Long> requestedIds = Arrays.asList(999L);
        when(pokemonService.getAllPokemon(requestedIds)).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<PokemonDTO>> response = pokemonController.getAllPokemon(requestedIds);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();

        verify(pokemonService, times(1)).getAllPokemon(requestedIds);
    }

    @Test
    @DisplayName("Should return empty list when no IDs provided and repository has no data")
    void getAllPokemon_WhenNoIdsProvidedAndNoData_ShouldReturnEmptyList() {
        // Given
        when(pokemonService.getAllPokemon(null)).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<PokemonDTO>> response = pokemonController.getAllPokemon(null);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();

        verify(pokemonService, times(1)).getAllPokemon(ArgumentMatchers.<List<Long>>isNull());
    }

    @Test
    @DisplayName("Should handle repository exception gracefully when no IDs provided")
    void getAllPokemon_WhenNoIdsProvidedAndRepositoryThrowsException_ShouldPropagateException() {
        // Given
        RuntimeException repositoryException = new RuntimeException("Database connection failed");
        when(pokemonService.getAllPokemon(null)).thenThrow(repositoryException);

        // When & Then
        try {
            pokemonController.getAllPokemon(null);
        } catch (RuntimeException e) {
            assertThat(e).isEqualTo(repositoryException);
            assertThat(e.getMessage()).isEqualTo("Database connection failed");
        }

        verify(pokemonService, times(1)).getAllPokemon(ArgumentMatchers.<List<Long>>isNull());
    }

    @Test
    @DisplayName("Should handle repository exception gracefully when IDs provided")
    void getAllPokemon_WhenIdsProvidedAndRepositoryThrowsException_ShouldPropagateException() {
        // Given
        List<Long> requestedIds = Arrays.asList(1L, 2L);
        RuntimeException repositoryException = new RuntimeException("Database connection failed");
        when(pokemonService.getAllPokemon(requestedIds)).thenThrow(repositoryException);

        // When & Then
        try {
            pokemonController.getAllPokemon(requestedIds);
        } catch (RuntimeException e) {
            assertThat(e).isEqualTo(repositoryException);
            assertThat(e.getMessage()).isEqualTo("Database connection failed");
        }

        verify(pokemonService, times(1)).getAllPokemon(requestedIds);
    }
}
