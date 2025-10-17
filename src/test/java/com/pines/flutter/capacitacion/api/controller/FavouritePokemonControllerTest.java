package com.pines.flutter.capacitacion.api.controller;

import com.pines.flutter.capacitacion.api.dto.AddFavouriteRequestDTO;
import com.pines.flutter.capacitacion.api.dto.PokemonDTO;
import com.pines.flutter.capacitacion.api.dto.FavouritePokemonDTO;
import com.pines.flutter.capacitacion.api.dto.SwapFavouritesRequestDTO;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("FavouritePokemonController Unit Tests")
class FavouritePokemonControllerTest {

    @Mock
    private FavouritePokemonService favouritePokemonService;

    @InjectMocks
    private FavouritePokemonController controller;

    private PokemonDTO charmanderDto;
    private PokemonDTO squirtleDto;

    @BeforeEach
    void setUp() {
        TypeDTO fire = new TypeDTO(1L, "FIRE");
        TypeDTO water = new TypeDTO(2L, "WATER");

        charmanderDto = new PokemonDTO(
                1L,
                "charmander",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/4.png",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/shiny/4.png",
                fire
        );

        squirtleDto = new PokemonDTO(
                2L,
                "squirtle",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/7.png",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/shiny/7.png",
                water
        );
    }

    @Test
    @DisplayName("GET /api/favourite-pokemon returns favourites with ranking and 200")
    void getFavourites_ReturnsOkWithBody() {
        // Given
        List<FavouritePokemonDTO> favourites = Arrays.asList(
                new FavouritePokemonDTO(charmanderDto, 1),
                new FavouritePokemonDTO(squirtleDto, 2)
        );
        when(favouritePokemonService.getFavouritePokemonForCurrentUser()).thenReturn(favourites);

        // When
        ResponseEntity<List<FavouritePokemonDTO>> response = controller.getFavourites();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(
                new FavouritePokemonDTO(charmanderDto, 1),
                new FavouritePokemonDTO(squirtleDto, 2)
        );
        verify(favouritePokemonService, times(1)).getFavouritePokemonForCurrentUser();
    }

    @Test
    @DisplayName("POST /api/favourite-pokemon adds favourite with ranking and returns 201")
    void addFavourite_ReturnsCreatedWithBody() {
        // Given
        AddFavouriteRequestDTO request = new AddFavouriteRequestDTO(1L);
        when(favouritePokemonService.addFavouritePokemonForCurrentUser(1L)).thenReturn(new FavouritePokemonDTO(charmanderDto, 1));

        // When
        ResponseEntity<FavouritePokemonDTO> response = controller.addFavourite(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(new FavouritePokemonDTO(charmanderDto, 1));
        verify(favouritePokemonService, times(1)).addFavouritePokemonForCurrentUser(1L);
    }

    @Test
    @DisplayName("DELETE /api/favourite-pokemon/{id} removes favourite and returns 204")
    void removeFavourite_ReturnsNoContent() {
        // When
        ResponseEntity<Void> response = controller.removeFavourite(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(favouritePokemonService, times(1)).removeFavouritePokemonForCurrentUser(1L);
    }

    @Test
    @DisplayName("PATCH /api/favourite-pokemon swaps favourites and returns 204")
    void swapFavourites_ReturnsNoContent() {
        // Given
        SwapFavouritesRequestDTO request = new SwapFavouritesRequestDTO(1, 2);

        // When
        ResponseEntity<Void> response = controller.swapFavourites(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(favouritePokemonService, times(1)).swapFavouritesForCurrentUser(1, 2);
    }
}
