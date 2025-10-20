package com.pines.flutter.capacitacion.api.service;

import com.pines.flutter.capacitacion.api.dto.PokemonDTO;
import com.pines.flutter.capacitacion.api.dto.TypeDTO;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PokemonService Unit Tests")
class PokemonServiceTest {

    @Mock
    private PokemonRepository pokemonRepository;

    @Mock
    private PokemonMapper pokemonMapper;

    @InjectMocks
    private PokemonService pokemonService;

    private Pokemon charmander;
    private Pokemon squirtle;
    private PokemonDTO charmanderDto;
    private PokemonDTO squirtleDto;

    @BeforeEach
    void setUp() {
        PokemonType fireType = new PokemonType();
        fireType.setId(1L);
        fireType.setName(PokemonType.TypeName.FIRE);

        PokemonType waterType = new PokemonType();
        waterType.setId(2L);
        waterType.setName(PokemonType.TypeName.WATER);

        charmander = new Pokemon();
        charmander.setId(1L);
        charmander.setName("charmander");
        charmander.setPicture("pic-4");
        charmander.setShinyPicture("shiny-4");
        charmander.setType(fireType);

        squirtle = new Pokemon();
        squirtle.setId(2L);
        squirtle.setName("squirtle");
        squirtle.setPicture("pic-7");
        squirtle.setShinyPicture("shiny-7");
        squirtle.setType(waterType);

        charmanderDto = new PokemonDTO(1L, "charmander", "pic-4", "shiny-4", new TypeDTO(1L, "FIRE"));
        squirtleDto = new PokemonDTO(2L, "squirtle", "pic-7", "shiny-7", new TypeDTO(2L, "WATER"));

        lenient().when(pokemonMapper.toDto(charmander)).thenReturn(charmanderDto);
        lenient().when(pokemonMapper.toDto(squirtle)).thenReturn(squirtleDto);
    }

    @Test
    @DisplayName("Should return all Pokemon when ids is null")
    void getAllPokemon_NullIds_ReturnsAll() {
        when(pokemonRepository.findAll()).thenReturn(Arrays.asList(charmander, squirtle));

        List<PokemonDTO> result = pokemonService.getAllPokemon(null);

        assertThat(result).containsExactlyInAnyOrder(charmanderDto, squirtleDto);
        verify(pokemonRepository, times(1)).findAll();
        verify(pokemonRepository, never()).findAllById(any());
    }

    @Test
    @DisplayName("Should return all Pokemon when ids is empty")
    void getAllPokemon_EmptyIds_ReturnsAll() {
        when(pokemonRepository.findAll()).thenReturn(Arrays.asList(charmander, squirtle));

        List<PokemonDTO> result = pokemonService.getAllPokemon(Collections.emptyList());

        assertThat(result).containsExactlyInAnyOrder(charmanderDto, squirtleDto);
        verify(pokemonRepository, times(1)).findAll();
        verify(pokemonRepository, never()).findAllById(any());
    }

    @Test
    @DisplayName("Should query by ids when provided")
    void getAllPokemon_WithIds_Filters() {
        List<Long> ids = Arrays.asList(1L, 2L);
        when(pokemonRepository.findAllById(ids)).thenReturn(Arrays.asList(charmander, squirtle));

        List<PokemonDTO> result = pokemonService.getAllPokemon(ids);

        assertThat(result).containsExactlyInAnyOrder(charmanderDto, squirtleDto);
        verify(pokemonRepository, times(1)).findAllById(ids);
        verify(pokemonRepository, never()).findAll();
    }

    @Test
    @DisplayName("Should return empty list when repository returns none")
    void getAllPokemon_NoResults_ReturnsEmpty() {
        List<Long> ids = Arrays.asList(999L);
        when(pokemonRepository.findAllById(ids)).thenReturn(Collections.emptyList());

        List<PokemonDTO> result = pokemonService.getAllPokemon(ids);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should propagate repository exception")
    void getAllPokemon_RepositoryThrows_Propagates() {
        when(pokemonRepository.findAll()).thenThrow(new RuntimeException("DB down"));

        assertThatThrownBy(() -> pokemonService.getAllPokemon(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB down");
    }
}


