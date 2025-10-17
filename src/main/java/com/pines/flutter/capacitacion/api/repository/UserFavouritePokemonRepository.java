package com.pines.flutter.capacitacion.api.repository;

import com.pines.flutter.capacitacion.api.model.user.UserFavouritePokemon;
import com.pines.flutter.capacitacion.api.model.user.UserFavouritePokemonId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserFavouritePokemonRepository extends JpaRepository<UserFavouritePokemon, UserFavouritePokemonId> {

    List<UserFavouritePokemon> findAllByUser_IdOrderByRankingNumberAsc(Long userId);

    Optional<UserFavouritePokemon> findByUser_IdAndRankingNumber(Long userId, Integer rankingNumber);

    Optional<UserFavouritePokemon> findByUser_IdAndPokemon_Id(Long userId, Long pokemonId);

    boolean existsByUser_IdAndPokemon_Id(Long userId, Long pokemonId);
}


