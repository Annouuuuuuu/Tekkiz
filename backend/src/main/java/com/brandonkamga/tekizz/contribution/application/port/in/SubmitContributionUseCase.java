package com.brandonkamga.tekizz.contribution.application.port.in;

import com.brandonkamga.tekizz.domain.Question;
import com.brandonkamga.tekizz.domain.User;
import com.brandonkamga.tekizz.dto.contribution.ContributionQuestionRequest;

public interface SubmitContributionUseCase {
    Question submit(ContributionQuestionRequest request, User submittedBy);
}
