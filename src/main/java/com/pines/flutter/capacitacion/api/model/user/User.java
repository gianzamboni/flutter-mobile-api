package com.pines.flutter.capacitacion.api.model.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.pines.flutter.capacitacion.api.model.pokemon.Pokemon;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("rankingNumber ASC")
    private List<UserFavouritePokemon> favouritePokemon = new ArrayList<>();

    public void addFavouritePokemon(Pokemon pokemon) {
        int nextRank = favouritePokemon.size() + 1;
        UserFavouritePokemon link = new UserFavouritePokemon(
                new UserFavouritePokemonId(this.id, pokemon.getId()),
                this,
                pokemon,
                nextRank
        );
        favouritePokemon.add(link);
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
