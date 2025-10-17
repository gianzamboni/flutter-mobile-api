package com.pines.flutter.capacitacion.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FavouritePokemonDTO {
    private PokemonDTO pokemon;
    private Integer rankingNumber;
}


