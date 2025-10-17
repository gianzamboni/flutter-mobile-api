package com.pines.flutter.capacitacion.api.service;

import com.pines.flutter.capacitacion.api.dto.FavouritePokemonDTO;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.pines.flutter.capacitacion.api.mapper.PokemonMapper;
import com.pines.flutter.capacitacion.api.model.pokemon.Pokemon;
import com.pines.flutter.capacitacion.api.model.user.UserFavouritePokemon;
import com.pines.flutter.capacitacion.api.model.user.User;
import com.pines.flutter.capacitacion.api.repository.PokemonRepository;
import com.pines.flutter.capacitacion.api.repository.UserRepository;
import com.pines.flutter.capacitacion.api.repository.UserFavouritePokemonRepository;
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

    @Autowired
    private final UserFavouritePokemonRepository userFavouritePokemonRepository;

    public List<FavouritePokemonDTO> getFavouritePokemonForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());

        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + userId));

        List<UserFavouritePokemon> favourites = userFavouritePokemonRepository.findAllByUser_IdOrderByRankingNumberAsc(userId);
        return favourites.stream()
                .map(link -> new FavouritePokemonDTO(
                        pokemonMapper.toDto(link.getPokemon()),
                        link.getRankingNumber()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public FavouritePokemonDTO addFavouritePokemonForCurrentUser(Long pokemonId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + userId));

        Optional<UserFavouritePokemon> alreadyFavourite = userFavouritePokemonRepository.findByUser_IdAndPokemon_Id(userId, pokemonId);
        if (alreadyFavourite.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Pokemon is already in favourites");
        }

        Pokemon pokemon = pokemonRepository.findById(pokemonId)
                .orElseThrow(() -> new IllegalArgumentException("Pokemon not found: " + pokemonId));

        user.addFavouritePokemon(pokemon);
        userRepository.save(user);

        // After save, the new favourite should be at the end with ranking = size
        List<UserFavouritePokemon> savedFavourites = userFavouritePokemonRepository.findAllByUser_IdOrderByRankingNumberAsc(userId);
        UserFavouritePokemon last = savedFavourites.get(savedFavourites.size() - 1);
        return new FavouritePokemonDTO(pokemonMapper.toDto(last.getPokemon()), last.getRankingNumber());
    }

    @Transactional
    public void removeFavouritePokemonForCurrentUser(Long pokemonId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());

        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + userId));

        Optional<UserFavouritePokemon> favouritePokemonOptional = userFavouritePokemonRepository.findByUser_IdAndPokemon_Id(userId, pokemonId);
        if (favouritePokemonOptional.isEmpty()) {
            return;
        }

        UserFavouritePokemon toRemove = favouritePokemonOptional.get();
        userFavouritePokemonRepository.delete(toRemove);

        // shift down ranks above the removed rank
        List<UserFavouritePokemon> remaining = userFavouritePokemonRepository.findAllByUser_IdOrderByRankingNumberAsc(userId);
        int expected = 1;
        for (UserFavouritePokemon link : remaining) {
            if (!link.getRankingNumber().equals(expected)) {
                link.setRankingNumber(expected);
            }
            expected++;
        }
        userFavouritePokemonRepository.saveAll(remaining);
    }

    @Transactional
    public List<FavouritePokemonDTO> swapFavouritesForCurrentUser(Integer rankingNumber1, Integer rankingNumber2) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());

        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + userId));

        if (rankingNumber1.equals(rankingNumber2)) {
            return List.of();
        }

        UserFavouritePokemon first = userFavouritePokemonRepository.findByUser_IdAndRankingNumber(userId, rankingNumber1)
                .orElse(null);
        UserFavouritePokemon second = userFavouritePokemonRepository.findByUser_IdAndRankingNumber(userId, rankingNumber2)
                .orElse(null);
                
        if (first == null || second == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One or both Pokemon not found in favourites");
        }

        Integer rank1 = first.getRankingNumber();
        Integer rank2 = second.getRankingNumber();

        // safe swap avoiding unique constraint clash using a temporary sentinel rank
        int tempRank = -1;
        first.setRankingNumber(tempRank);
        userFavouritePokemonRepository.save(first);
        userFavouritePokemonRepository.flush();

        second.setRankingNumber(rank1);
        userFavouritePokemonRepository.save(second);
        userFavouritePokemonRepository.flush();

        first.setRankingNumber(rank2);
        userFavouritePokemonRepository.save(first);

        return List.of(
                new FavouritePokemonDTO(pokemonMapper.toDto(second.getPokemon()), rank1),
                new FavouritePokemonDTO(pokemonMapper.toDto(first.getPokemon()), rank2)
        );
    }

}

