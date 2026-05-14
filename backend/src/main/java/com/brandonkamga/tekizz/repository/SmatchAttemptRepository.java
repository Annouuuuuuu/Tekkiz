package com.brandonkamga.tekizz.repository;

import com.brandonkamga.tekizz.domain.SmatchAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SmatchAttemptRepository extends JpaRepository<SmatchAttempt, Long> {

    List<SmatchAttempt> findBySessionId(Long sessionId);

    long countBySessionId(Long sessionId);

    void deleteBySessionId(Long sessionId);
}
