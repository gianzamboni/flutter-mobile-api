package com.pines.flutter.capacitacion.api.controller;

import com.pines.flutter.capacitacion.api.model.pokemon.PokemonType;
import com.pines.flutter.capacitacion.api.repository.PokemonTypeRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PokemonTypeController Unit Tests")
class PokemonTypeControllerTest {

    @Mock
    private PokemonTypeRepository repository;

    @InjectMocks
    private PokemonTypeController controller;

    @Test
    @DisplayName("Should return all types with 200")
    void getAllPokemonTypes_ReturnsOk() {
        PokemonType t1 = new PokemonType();
        t1.setId(1L);
        PokemonType t2 = new PokemonType();
        t2.setId(2L);

        when(repository.findAll()).thenReturn(Arrays.asList(t1, t2));

        ResponseEntity<List<PokemonType>> response = controller.getAllPokemonTypes();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(t1, t2);
        verify(repository, times(1)).findAll();
    }
}


