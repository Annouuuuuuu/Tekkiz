package com.brandonkamga.tekizz.repository;

import com.brandonkamga.tekizz.domain.SmatchDeck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SmatchDeckRepository extends JpaRepository<SmatchDeck, Long> {

    List<SmatchDeck> findByIsActiveTrue();

    List<SmatchDeck> findByCategoryId(Long categoryId);

    Optional<SmatchDeck> findByName(String name);

    Page<SmatchDeck> findByIsActive(Boolean isActive, Pageable pageable);

    @Query("SELECT d FROM SmatchDeck d WHERE (:categoryId IS NULL OR d.category.id = :categoryId) AND (:isActive IS NULL OR d.isActive = :isActive)")
    Page<SmatchDeck> findFiltered(@Param("categoryId") Long categoryId,
                                  @Param("isActive") Boolean isActive,
                                  Pageable pageable);

    @Query("SELECT COUNT(d) FROM SmatchDeck d WHERE d.isActive = true")
    long countActive();
}
