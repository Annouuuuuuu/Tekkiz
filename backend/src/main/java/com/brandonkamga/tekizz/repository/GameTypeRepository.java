package com.brandonkamga.tekizz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brandonkamga.tekizz.domain.GameType;
import com.brandonkamga.tekizz.domain.GameTypeName;

@Repository
public interface GameTypeRepository extends JpaRepository<GameType, Long> {
    
    Optional<GameType> findByTypeName(GameTypeName typeName);
    
    boolean existsByTypeName(GameTypeName typeName);
}