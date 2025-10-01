package com.pines.flutter.capacitacion.api.model.pokemon;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pokemon_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PokemonType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private TypeName name;
    
    @Column(name = "light_color", length = 7) // For hex color codes like #FF0000
    private String lightColor;
    
    @Column(name = "dark_color", length = 7) // For hex color codes like #FF0000
    private String darkColor;
    
    public enum TypeName {
        NORMAL, FIGHT, FLYING, POISON, GROUND, ROCK, BUG, GHOST, STEEL, 
        FIRE, WATER, GRASS, ELECTRIC, PSYCHIC, ICE, DRAGON, DARK, FAIRY, 
        STELLAR, UNKNOWN, SHADOW
    }
}
