package com.brandonkamga.tekizz.admin.application.service;

import com.brandonkamga.tekizz.domain.SmatchDeck;
import com.brandonkamga.tekizz.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Admin application service for Smatch game management.
 */
@Service
@Transactional(readOnly = true)
public class AdminSmatchApplicationService {

    private final SmatchDeckRepository smatchDeckRepository;
    private final SmatchSessionRepository smatchSessionRepository;

    public AdminSmatchApplicationService(SmatchDeckRepository smatchDeckRepository,
                                          SmatchSessionRepository smatchSessionRepository) {
        this.smatchDeckRepository = smatchDeckRepository;
        this.smatchSessionRepository = smatchSessionRepository;
    }

    public List<SmatchDeck> getDecks() {
        return smatchDeckRepository.findAll();
    }
}
