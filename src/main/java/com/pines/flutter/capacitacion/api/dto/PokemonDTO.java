package com.pines.flutter.capacitacion.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PokemonDTO {
    private Long id;
    private String name;
    private String picture;
    private String shinyPicture;
    private Long typeId;
}
