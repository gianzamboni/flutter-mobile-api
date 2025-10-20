package com.pines.flutter.capacitacion.api.service;

import com.pines.flutter.capacitacion.api.dto.PokemonDTO;
import com.pines.flutter.capacitacion.api.model.pokemon.Pokemon;
import com.pines.flutter.capacitacion.api.repository.PokemonRepository;
import com.pines.flutter.capacitacion.api.mapper.PokemonMapper;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PokemonService {
    
    @Autowired
    private final PokemonRepository pokemonRepository;

    @Autowired
    private final PokemonMapper pokemonMapper;

    public List<PokemonDTO> getAllPokemon(List<Long> ids) {
        List<Pokemon> pokemon;
        
        if (ids == null || ids.isEmpty()) {
            pokemon = pokemonRepository.findAll();
        } else {
            pokemon = pokemonRepository.findAllById(ids);
        }

        return pokemon.stream()
                .map(pokemonMapper::toDto)
                .collect(Collectors.toList());
    }
}
