package com.pines.flutter.capacitacion.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwapFavouritesRequestDTO {
    @NotNull
    private Integer rankingNumber1;

    @NotNull
    private Integer rankingNumber2;
}


