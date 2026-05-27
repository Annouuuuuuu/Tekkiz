package com.brandonkamga.tekizz.contribution.application.port.in;

import com.brandonkamga.tekizz.domain.Question;
import java.util.List;

public interface GetMyContributionsUseCase {
    List<Question> getMyContributions(Long userId);
}
