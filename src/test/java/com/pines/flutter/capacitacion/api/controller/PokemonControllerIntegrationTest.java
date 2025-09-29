package com.pines.flutter.capacitacion.api.controller;

import com.pines.flutter.capacitacion.api.model.pokemon.Pokemon;
import com.pines.flutter.capacitacion.api.model.pokemon.PokemonType;
import com.pines.flutter.capacitacion.api.repository.PokemonRepository;
import com.pines.flutter.capacitacion.api.repository.PokemonTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("PokemonController Integration Tests")
class PokemonControllerIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PokemonRepository pokemonRepository;

    @Autowired
    private PokemonTypeRepository pokemonTypeRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        setupTestData();
    }

    private void setupTestData() {
        // Clear existing data
        pokemonRepository.deleteAll();
        pokemonTypeRepository.deleteAll();

        // Create PokemonType test data
        PokemonType fireType = new PokemonType();
        fireType.setName(PokemonType.TypeName.FIRE);
        fireType.setLightColor("#F08030");
        fireType.setDarkColor("#D06010");
        fireType = pokemonTypeRepository.save(fireType);

        PokemonType waterType = new PokemonType();
        waterType.setName(PokemonType.TypeName.WATER);
        waterType.setLightColor("#6890F0");
        waterType.setDarkColor("#4870D0");
        waterType = pokemonTypeRepository.save(waterType);

        PokemonType grassType = new PokemonType();
        grassType.setName(PokemonType.TypeName.GRASS);
        grassType.setLightColor("#78C850");
        grassType.setDarkColor("#58A830");
        grassType = pokemonTypeRepository.save(grassType);

        // Create Pokemon test data
        Pokemon charmander = new Pokemon();
        charmander.setName("charmander");
        charmander.setPicture("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/4.png");
        charmander.setShinyPicture("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/shiny/4.png");
        charmander.setType(fireType);
        pokemonRepository.save(charmander);

        Pokemon squirtle = new Pokemon();
        squirtle.setName("squirtle");
        squirtle.setPicture("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/7.png");
        squirtle.setShinyPicture("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/shiny/7.png");
        squirtle.setType(waterType);
        pokemonRepository.save(squirtle);

        Pokemon bulbasaur = new Pokemon();
        bulbasaur.setName("bulbasaur");
        bulbasaur.setPicture("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png");
        bulbasaur.setShinyPicture("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/shiny/1.png");
        bulbasaur.setType(grassType);
        pokemonRepository.save(bulbasaur);
    }

    @Test
    @DisplayName("Should return all Pokemon with HTTP 200 status")
    void getAllPokemon_ShouldReturnAllPokemonWith200Status() throws Exception {
        mockMvc.perform(get("/pokemon")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("charmander", "squirtle", "bulbasaur")))
                .andExpect(jsonPath("$[*].id", notNullValue()))
                .andExpect(jsonPath("$[*].picture", notNullValue()))
                .andExpect(jsonPath("$[*].shinyPicture", notNullValue()))
                .andExpect(jsonPath("$[*].typeId", notNullValue()));
    }

    @Test
    @DisplayName("Should return empty list when no Pokemon exist")
    void getAllPokemon_WhenNoPokemon_ShouldReturnEmptyList() throws Exception {
        // Clear all Pokemon
        pokemonRepository.deleteAll();

        mockMvc.perform(get("/pokemon")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    @DisplayName("Should return Pokemon with correct JSON structure")
    void getAllPokemon_ShouldReturnCorrectJsonStructure() throws Exception {
        mockMvc.perform(get("/pokemon")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", isA(Number.class)))
                .andExpect(jsonPath("$[0].name", isA(String.class)))
                .andExpect(jsonPath("$[0].picture", isA(String.class)))
                .andExpect(jsonPath("$[0].shinyPicture", isA(String.class)))
                .andExpect(jsonPath("$[0].typeId", isA(Number.class)));
    }

    @Test
    @DisplayName("Should handle large dataset efficiently")
    void getAllPokemon_WithLargeDataset_ShouldHandleEfficiently() throws Exception {
        // Add more Pokemon to test with larger dataset
        PokemonType fireType = pokemonTypeRepository.findByName(PokemonType.TypeName.FIRE).orElseThrow();
        
        for (int i = 0; i < 10; i++) {
            Pokemon pokemon = new Pokemon();
            pokemon.setName("test-pokemon-" + i);
            pokemon.setPicture("https://example.com/pokemon" + i + ".png");
            pokemon.setShinyPicture("https://example.com/shiny-pokemon" + i + ".png");
            pokemon.setType(fireType);
            pokemonRepository.save(pokemon);
        }

        mockMvc.perform(get("/pokemon")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(13))) // 3 original + 10 new
                .andExpect(jsonPath("$[*].name", hasItem(startsWith("test-pokemon-"))));
    }
}
