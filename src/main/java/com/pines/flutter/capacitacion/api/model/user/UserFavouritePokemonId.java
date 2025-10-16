package com.pines.flutter.capacitacion.api.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFavouritePokemonId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "pokemon_id")
    private Long pokemonId;
}


