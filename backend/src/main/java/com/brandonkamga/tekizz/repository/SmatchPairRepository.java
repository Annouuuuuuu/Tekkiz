package com.brandonkamga.tekizz.repository;

import com.brandonkamga.tekizz.domain.SmatchPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SmatchPairRepository extends JpaRepository<SmatchPair, Long> {

    List<SmatchPair> findByDeckId(Long deckId);

    List<SmatchPair> findByDeckIdAndIsActiveTrue(Long deckId);

    long countByDeckId(Long deckId);

    long countByDeckIdAndIsActiveTrue(Long deckId);

    void deleteByDeckId(Long deckId);
}
