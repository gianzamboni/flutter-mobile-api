package com.pines.flutter.capacitacion.api.service;

import com.pines.flutter.capacitacion.api.dto.PokemonDTO;
import com.pines.flutter.capacitacion.api.mapper.PokemonMapper;
import com.pines.flutter.capacitacion.api.model.pokemon.Pokemon;
import com.pines.flutter.capacitacion.api.model.user.User;
import com.pines.flutter.capacitacion.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavouritePokemonService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PokemonMapper pokemonMapper;

    public List<PokemonDTO> getFavouritePokemonForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());

        User user = userRepository.findWithFavouritePokemonById(userId)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + userId));

        Set<Pokemon> favourites = user.getFavouritePokemon();
        return favourites.stream()
                .map(pokemonMapper::toDto)
                .collect(Collectors.toList());
    }
}


