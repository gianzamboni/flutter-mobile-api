package com.pines.flutter.capacitacion.api.repository;

import com.pines.flutter.capacitacion.api.model.pokemon.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Long> {}
