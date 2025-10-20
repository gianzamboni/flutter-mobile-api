package com.pines.flutter.capacitacion.api.repository;

import com.pines.flutter.capacitacion.api.model.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    @EntityGraph(attributePaths = {"favouritePokemon", "favouritePokemon.pokemon", "favouritePokemon.pokemon.type"})
    Optional<User> findWithFavouritePokemonByUsername(String username);

    @EntityGraph(attributePaths = {"favouritePokemon", "favouritePokemon.pokemon", "favouritePokemon.pokemon.type"})
    Optional<User> findWithFavouritePokemonById(Long id);
}
