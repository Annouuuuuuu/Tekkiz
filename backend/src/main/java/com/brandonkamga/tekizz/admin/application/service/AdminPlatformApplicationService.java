package com.brandonkamga.tekizz.admin.application.service;

import com.brandonkamga.tekizz.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Admin application service for platform-wide management.
 */
@Service
@Transactional(readOnly = true)
public class AdminPlatformApplicationService {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final GameSessionRepository gameSessionRepository;
    private final SmatchDeckRepository smatchDeckRepository;
    private final SmatchPairRepository smatchPairRepository;
    private final SmatchSessionRepository smatchSessionRepository;

    public AdminPlatformApplicationService(UserRepository userRepository,
                                            QuestionRepository questionRepository,
                                            CategoryRepository categoryRepository,
                                            GameSessionRepository gameSessionRepository,
                                            SmatchDeckRepository smatchDeckRepository,
                                            SmatchPairRepository smatchPairRepository,
                                            SmatchSessionRepository smatchSessionRepository) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
        this.gameSessionRepository = gameSessionRepository;
        this.smatchDeckRepository = smatchDeckRepository;
        this.smatchPairRepository = smatchPairRepository;
        this.smatchSessionRepository = smatchSessionRepository;
    }

    public long countUsers() { return userRepository.count(); }
    public long countQuestions() { return questionRepository.count(); }
    public long countCategories() { return categoryRepository.count(); }
    public long countQcmSessions() { return gameSessionRepository.count(); }
    public long countSmatchDecks() { return smatchDeckRepository.count(); }
}
