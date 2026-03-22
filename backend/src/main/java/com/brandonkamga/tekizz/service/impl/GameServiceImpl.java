package com.brandonkamga.tekizz.service.impl;

import com.brandonkamga.tekizz.domain.Game;
import com.brandonkamga.tekizz.exception.ResourceNotFoundException;
import com.brandonkamga.tekizz.repository.GameRepository;
import com.brandonkamga.tekizz.service.interfaces.GameService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of GameService.
 * Follows Single Responsibility Principle.
 */
@Service
@Transactional
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Game> findById(Long id) {
        return gameRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Game> findByName(String name) {
        return gameRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Game> findAll() {
        return gameRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Game> findAllOrderByName() {
        return gameRepository.findAllByOrderByNameAsc();
    }

    @Override
    public Game save(Game game) {
        return gameRepository.save(game);
    }

    @Override
    public void deleteById(Long id) {
        if (!gameRepository.existsById(id)) {
            throw new ResourceNotFoundException("Game", "id", id);
        }
        gameRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return gameRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return gameRepository.existsById(id);
    }
}
