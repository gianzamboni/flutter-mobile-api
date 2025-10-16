package com.pines.flutter.capacitacion.api.controller;

import com.pines.flutter.capacitacion.api.model.pokemon.Pokemon;
import com.pines.flutter.capacitacion.api.model.pokemon.PokemonType;
import com.pines.flutter.capacitacion.api.model.user.User;
import com.pines.flutter.capacitacion.api.repository.PokemonRepository;
import com.pines.flutter.capacitacion.api.repository.PokemonTypeRepository;
import com.pines.flutter.capacitacion.api.repository.UserRepository;
import com.pines.flutter.capacitacion.api.util.TestDataUtil;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("FavouritePokemonController Integration Tests")
class FavouritePokemonControllerIT {

    @Container
    @SuppressWarnings("resource")
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

    @Autowired
    private UserRepository userRepository;


    private MockMvc mockMvc;

    private Long userId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        setupTestData();
    }

    private void setupTestData() {
        TestDataUtil.clearAll(pokemonRepository, pokemonTypeRepository, userRepository);
        PokemonType fireType = TestDataUtil.saveType(pokemonTypeRepository, PokemonType.TypeName.FIRE, null, null);
        Pokemon charmander = TestDataUtil.savePokemon(pokemonRepository, "charmander", "pic-4", "shiny-4", fireType);
        User user = TestDataUtil.saveUserWithFavourite(userRepository, charmander, "Ash", "Ketchum", "ash", "enc");
        userId = user.getId();
    }

    @Test
    @DisplayName("Should return user's favourite Pokemon when authenticated")
    void getFavourites_ShouldReturnFavourites() throws Exception {
        // Manually set authentication in SecurityContext for test profile
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(String.valueOf(userId), null, java.util.Collections.emptyList())
        );

        mockMvc.perform(get("/api/favourite-pokemon")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("charmander")))
                .andExpect(jsonPath("$[0].id", notNullValue()));
    }

    @Test
    @DisplayName("Should return empty list when user has no favourites")
    void getFavourites_WhenNone_ShouldReturnEmpty() throws Exception {
        // Create another user without favourites
        User user = new User();
        user.setName("Misty");
        user.setSurname("Waterflower");
        user.setUsername("misty");
        user.setPassword("enc");
        user = userRepository.save(user);
        // Manually set authentication in SecurityContext for test profile
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(String.valueOf(user.getId()), null, java.util.Collections.emptyList())
        );

        mockMvc.perform(get("/api/favourite-pokemon")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }
}


