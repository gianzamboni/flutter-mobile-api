package com.pines.flutter.capacitacion.api.repository;

import com.pines.flutter.capacitacion.api.model.pokemon.PokemonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PokemonTypeRepository extends JpaRepository<PokemonType, Long> {
    
    Optional<PokemonType> findByName(PokemonType.TypeName name);
    
    boolean existsByName(PokemonType.TypeName name);
}
