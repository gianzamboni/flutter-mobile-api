package com.pines.flutter.capacitacion.api.mapper;

import com.pines.flutter.capacitacion.api.dto.PokemonDTO;
import com.pines.flutter.capacitacion.api.model.pokemon.Pokemon;
import com.pines.flutter.capacitacion.api.model.pokemon.PokemonType;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.pines.flutter.capacitacion.api.dto.TypeDTO;

@Mapper(componentModel = "spring")
public interface PokemonMapper {
    PokemonDTO toDto(Pokemon pokemon);

    @Mapping(target = "type", ignore = true)
    Pokemon toEntity(PokemonDTO pokemonDTO);
    
    TypeDTO typeToDto(PokemonType type);
}
