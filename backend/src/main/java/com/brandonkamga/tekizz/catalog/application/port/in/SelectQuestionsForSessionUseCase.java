package com.brandonkamga.tekizz.catalog.application.port.in;

import com.brandonkamga.tekizz.domain.Question;
import com.brandonkamga.tekizz.domain.QuestionLevelType;
import java.util.List;

public interface SelectQuestionsForSessionUseCase {
    List<Question> selectForSession(Long categoryId, Long gameId, QuestionLevelType level,
                                    List<Long> tagIds, List<Long> excludeIds);
}
