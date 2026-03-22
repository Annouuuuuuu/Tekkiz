package com.brandonkamga.tekizz.service.interfaces;

import com.brandonkamga.tekizz.domain.Game;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Game operations.
 * Follows Interface Segregation Principle.
 */
public interface GameService {

    /**
     * Find a game by its ID.
     *
     * @param id the game ID
     * @return the game if found
     */
    Optional<Game> findById(Long id);

    /**
     * Find a game by its name.
     *
     * @param name the game name
     * @return the game if found
     */
    Optional<Game> findByName(String name);

    /**
     * Find all games.
     *
     * @return list of all games
     */
    List<Game> findAll();

    /**
     * Find all games ordered by name.
     *
     * @return list of games ordered by name
     */
    List<Game> findAllOrderByName();

    /**
     * Save a game.
     *
     * @param game the game to save
     * @return the saved game
     */
    Game save(Game game);

    /**
     * Delete a game by its ID.
     *
     * @param id the game ID
     */
    void deleteById(Long id);

    /**
     * Check if a game exists by name.
     *
     * @param name the game name
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Check if a game exists by ID.
     *
     * @param id the game ID
     * @return true if exists, false otherwise
     */
    boolean existsById(Long id);
}
