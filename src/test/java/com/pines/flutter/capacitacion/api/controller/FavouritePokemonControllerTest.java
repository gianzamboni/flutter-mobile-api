package com.pines.flutter.capacitacion.api.controller;

import com.pines.flutter.capacitacion.api.dto.PokemonDTO;
import com.pines.flutter.capacitacion.api.dto.TypeDTO;
import com.pines.flutter.capacitacion.api.service.FavouritePokemonService;
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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("FavouritePokemonController Unit Tests")
class FavouritePokemonControllerTest {

    @Mock
    private FavouritePokemonService favouritePokemonService;

    @InjectMocks
    private FavouritePokemonController favouritePokemonController;

    private TypeDTO fireTypeDto;
    private PokemonDTO charmanderDto;

    @BeforeEach
    void setUp() {
        fireTypeDto = new TypeDTO(1L, "FIRE");
        charmanderDto = new PokemonDTO(
                1L,
                "charmander",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/4.png",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/shiny/4.png",
                fireTypeDto
        );

        lenient().when(favouritePokemonService.getFavouritePokemonForCurrentUser())
                .thenReturn(Collections.singletonList(charmanderDto));
    }

    @Test
    @DisplayName("Should return favourite Pokemon with HTTP 200 status")
    void getFavourites_ShouldReturnFavouritesWith200Status() {
        // Given
        List<PokemonDTO> expected = Arrays.asList(charmanderDto);
        when(favouritePokemonService.getFavouritePokemonForCurrentUser()).thenReturn(expected);

        // When
        ResponseEntity<List<PokemonDTO>> response = favouritePokemonController.getFavourites();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsExactlyElementsOf(expected);
        verify(favouritePokemonService, times(1)).getFavouritePokemonForCurrentUser();
    }

    @Test
    @DisplayName("Should return empty list when user has no favourites")
    void getFavourites_WhenEmpty_ShouldReturnEmptyList() {
        // Given
        when(favouritePokemonService.getFavouritePokemonForCurrentUser()).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<PokemonDTO>> response = favouritePokemonController.getFavourites();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
        verify(favouritePokemonService, times(1)).getFavouritePokemonForCurrentUser();
    }
}


