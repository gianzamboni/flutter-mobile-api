package com.pines.flutter.capacitacion.api.util;

import com.pines.flutter.capacitacion.api.model.pokemon.Pokemon;
import com.pines.flutter.capacitacion.api.model.pokemon.PokemonType;
import com.pines.flutter.capacitacion.api.model.user.User;
import com.pines.flutter.capacitacion.api.repository.PokemonRepository;
import com.pines.flutter.capacitacion.api.repository.PokemonTypeRepository;
import com.pines.flutter.capacitacion.api.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public final class TestDataUtil {

    private TestDataUtil() {}

    public static void clearAll(PokemonRepository pokemonRepository, PokemonTypeRepository pokemonTypeRepository) {
        pokemonRepository.deleteAll();
        pokemonTypeRepository.deleteAll();
    }

    public static void clearAll(PokemonRepository pokemonRepository,
                                PokemonTypeRepository pokemonTypeRepository,
                                UserRepository userRepository) {
        pokemonRepository.deleteAll();
        pokemonTypeRepository.deleteAll();
        userRepository.deleteAll();
    }

    public static PokemonType saveType(PokemonTypeRepository typeRepository,
                                       PokemonType.TypeName name,
                                       String lightColor,
                                       String darkColor) {
        PokemonType type = new PokemonType();
        type.setName(name);
        if (lightColor != null) type.setLightColor(lightColor);
        if (darkColor != null) type.setDarkColor(darkColor);
        return typeRepository.save(type);
    }

    public static Pokemon savePokemon(PokemonRepository pokemonRepository,
                                      String name,
                                      String picture,
                                      String shinyPicture,
                                      PokemonType type) {
        Pokemon pokemon = new Pokemon();
        pokemon.setName(name);
        pokemon.setPicture(picture);
        pokemon.setShinyPicture(shinyPicture);
        pokemon.setType(type);
        return pokemonRepository.save(pokemon);
    }

    public static List<Pokemon> seedDefaultPokemonSet(PokemonRepository pokemonRepository,
                                                      PokemonTypeRepository typeRepository) {
        List<Pokemon> result = new ArrayList<>();

        PokemonType fireType = saveType(typeRepository, PokemonType.TypeName.FIRE, "#F08030", "#D06010");
        PokemonType waterType = saveType(typeRepository, PokemonType.TypeName.WATER, "#6890F0", "#4870D0");
        PokemonType grassType = saveType(typeRepository, PokemonType.TypeName.GRASS, "#78C850", "#58A830");

        result.add(savePokemon(pokemonRepository,
                "charmander",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/4.png",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/shiny/4.png",
                fireType));
        result.add(savePokemon(pokemonRepository,
                "squirtle",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/7.png",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/shiny/7.png",
                waterType));
        result.add(savePokemon(pokemonRepository,
                "bulbasaur",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/shiny/1.png",
                grassType));

        return result;
    }

    public static User saveUser(UserRepository userRepository,
                                String name,
                                String surname,
                                String username,
                                String password) {
        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setUsername(username);
        user.setPassword(password);
        return userRepository.save(user);
    }

    public static User saveUserWithFavourite(UserRepository userRepository,
                                             Pokemon favourite,
                                             String name,
                                             String surname,
                                             String username,
                                             String password) {
        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setUsername(username);
        user.setPassword(password);
        user.addFavouritePokemon(favourite);
        return userRepository.save(user);
    }
}


