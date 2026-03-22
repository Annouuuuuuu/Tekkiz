package com.brandonkamga.tekizz.repository;

import com.brandonkamga.tekizz.domain.Answer;
import com.brandonkamga.tekizz.domain.Question;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    
    List<Answer> findByQuestion(Question question);
    
    List<Answer> findByQuestionId(Long questionId);
    
    List<Answer> findByQuestionAndIsActiveTrue(Question question);
    
    List<Answer> findByQuestionIdAndIsActiveTrue(Long questionId);
    
    List<Answer> findByQuestionIdAndIsCorrectTrue(Long questionId);
    
    void deleteByQuestionId(Long questionId);
    
    long countByQuestionId(Long questionId);
    
    long countByQuestionIdAndIsCorrectTrue(Long questionId);
}
