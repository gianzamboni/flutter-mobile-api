package com.pines.flutter.capacitacion.api.dto;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonFilter("PokemonDTOFilter")
public class PokemonDTO {
    private Long id;
    private String name;
    private String picture;
    private String shinyPicture;
    private TypeDTO type;
}
