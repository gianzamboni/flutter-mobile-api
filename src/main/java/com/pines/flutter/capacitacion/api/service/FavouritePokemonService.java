package com.pines.flutter.capacitacion.api.service;

import com.pines.flutter.capacitacion.api.dto.PokemonDTO;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.pines.flutter.capacitacion.api.mapper.PokemonMapper;
import com.pines.flutter.capacitacion.api.model.pokemon.Pokemon;
import com.pines.flutter.capacitacion.api.model.user.UserFavouritePokemon;
import com.pines.flutter.capacitacion.api.model.user.User;
import com.pines.flutter.capacitacion.api.repository.PokemonRepository;
import com.pines.flutter.capacitacion.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavouritePokemonService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PokemonMapper pokemonMapper;

    @Autowired
    private final PokemonRepository pokemonRepository;

    public List<PokemonDTO> getFavouritePokemonForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());

        User user = userRepository.findWithFavouritePokemonById(userId)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + userId));

        List<UserFavouritePokemon> favourites = user.getFavouritePokemon();
        return favourites.stream()
                .map(UserFavouritePokemon::getPokemon)
                .map(pokemonMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PokemonDTO addFavouritePokemonForCurrentUser(Long pokemonId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());

        User user = userRepository.findWithFavouritePokemonById(userId)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + userId));

        Optional<UserFavouritePokemon> alreadyFavourite = user.getFavouritePokemon().stream()
                .filter(link -> link.getPokemon() != null && pokemonId.equals(link.getPokemon().getId()))
                .findFirst();
        if (alreadyFavourite.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Pokemon is already in favourites");
        }

        Pokemon pokemon = pokemonRepository.findById(pokemonId)
                .orElseThrow(() -> new IllegalArgumentException("Pokemon not found: " + pokemonId));

        user.addFavouritePokemon(pokemon);
        userRepository.save(user);
        return pokemonMapper.toDto(pokemon);
    }

    @Transactional
    public void removeFavouritePokemonForCurrentUser(Long pokemonId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());

        User user = userRepository.findWithFavouritePokemonById(userId)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + userId));

        boolean removed = user.getFavouritePokemon().removeIf(pokemonRanking ->
                pokemonRanking.getPokemon() != null && pokemonId.equals(pokemonRanking.getPokemon().getId())
        );

        if (removed) {
            // Re-compact ranking numbers to keep ordering contiguous
            int rank = 1;
            for (UserFavouritePokemon link : user.getFavouritePokemon()) {
                link.setRankingNumber(rank++);
            }
            userRepository.save(user);
        }
    }

    @Transactional
    public void swapFavouritesForCurrentUser(Long pokemonId1, Long pokemonId2) {
        if (pokemonId1 == null || pokemonId2 == null || pokemonId1.equals(pokemonId2)) {
            return; // nothing to swap
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());

        User user = userRepository.findWithFavouritePokemonById(userId)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + userId));

        UserFavouritePokemon first = null;
        UserFavouritePokemon second = null;
        for (UserFavouritePokemon pokemonRanking : user.getFavouritePokemon()) {
            if (pokemonRanking.getPokemon() == null) continue;
            Long id = pokemonRanking.getPokemon().getId();
            if (first == null && pokemonId1.equals(id)) {
                first = pokemonRanking;
            } else if (second == null && pokemonId2.equals(id)) {
                second = pokemonRanking;
            }
            if (first != null && second != null) break;
        }

        if (first == null || second == null) {
            return; // one or both not present; no-op
        }

        Integer rank1 = first.getRankingNumber();
        Integer rank2 = second.getRankingNumber();
        first.setRankingNumber(rank2);
        second.setRankingNumber(rank1);
        userRepository.save(user);
    }
}


