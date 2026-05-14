package com.brandonkamga.tekizz.repository;

import com.brandonkamga.tekizz.domain.SmatchSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SmatchSessionRepository extends JpaRepository<SmatchSession, Long> {

    List<SmatchSession> findByUserId(Long userId);

    List<SmatchSession> findByDeckId(Long deckId);

    Page<SmatchSession> findAll(Pageable pageable);

    long countByCompletedAtIsNotNull();

    long countByCompletedAtIsNull();
}
