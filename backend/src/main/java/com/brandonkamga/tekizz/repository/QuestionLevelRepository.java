package com.brandonkamga.tekizz.repository;

import com.brandonkamga.tekizz.domain.QuestionLevel;
import com.brandonkamga.tekizz.domain.QuestionLevelType;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionLevelRepository extends JpaRepository<QuestionLevel, Long> {
    
    Optional<QuestionLevel> findByLevelName(QuestionLevelType levelName);
    
    boolean existsByLevelName(QuestionLevelType levelName);
    
    List<QuestionLevel> findAllByOrderByLevelNameAsc();
}
