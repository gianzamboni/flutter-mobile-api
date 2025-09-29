package com.pines.flutter.capacitacion.api.mapper;

import com.pines.flutter.capacitacion.api.dto.PokemonDTO;
import com.pines.flutter.capacitacion.api.model.pokemon.Pokemon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for converting between Pokemon entities and DTOs.
 * 
 * MapStruct generates implementation at compile time, providing:
 * - High performance (no reflection)
 * - Type safety
 * - Automatic mapping for matching field names
 * - Custom mapping for complex fields (like type.id -> typeId)
 */
@Mapper(componentModel = "spring")
public interface PokemonMapper {

    /**
     * Converts a Pokemon entity to PokemonDTO.
     * Maps the nested type.id field to the flat typeId field.
     * 
     * @param pokemon the Pokemon entity
     * @return the corresponding PokemonDTO
     */
    @Mapping(source = "type.id", target = "typeId")
    PokemonDTO toDto(Pokemon pokemon);

    /**
     * Converts a PokemonDTO to Pokemon entity.
     * Note: This method requires the type to be set separately
     * as we only have the typeId in the DTO.
     * 
     * @param pokemonDTO the PokemonDTO
     * @return the corresponding Pokemon entity (without type set)
     */
    @Mapping(target = "type", ignore = true)
    Pokemon toEntity(PokemonDTO pokemonDTO);
}
