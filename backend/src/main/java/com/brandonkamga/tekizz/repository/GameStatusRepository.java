package com.brandonkamga.tekizz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brandonkamga.tekizz.domain.GameStatus;
import com.brandonkamga.tekizz.domain.GameStatusType;

@Repository
public interface GameStatusRepository extends JpaRepository<GameStatus, Long> {
    
    Optional<GameStatus> findByStatusName(GameStatusType statusName);
    
    boolean existsByStatusName(GameStatusType statusName);
}