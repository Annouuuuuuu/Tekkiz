package com.brandonkamga.tekizz.repository;

import com.brandonkamga.tekizz.domain.QuestionStatus;
import com.brandonkamga.tekizz.domain.QuestionStatusType;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionStatusRepository extends JpaRepository<QuestionStatus, Long> {
    
    Optional<QuestionStatus> findByStatusName(QuestionStatusType statusName);
    
    boolean existsByStatusName(QuestionStatusType statusName);
    
    List<QuestionStatus> findAllByOrderByStatusNameAsc();
}
