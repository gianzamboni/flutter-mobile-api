package com.pines.flutter.capacitacion.api.model.user;

import com.pines.flutter.capacitacion.api.model.pokemon.Pokemon;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "user_favourite_pokemon",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_fav_rank_per_user", columnNames = {"user_id", "ranking_number"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFavouritePokemon {

    @EmbeddedId
    private UserFavouritePokemonId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("pokemonId")
    @JoinColumn(name = "pokemon_id", nullable = false)
    private Pokemon pokemon;

    @Column(name = "ranking_number", nullable = false)
    private Integer rankingNumber;
}


