package com.brandonkamga.tekizz.contribution.application.port.in;

public interface ReviewContributionUseCase {
    void approve(Long questionId, String newStatus);
}
