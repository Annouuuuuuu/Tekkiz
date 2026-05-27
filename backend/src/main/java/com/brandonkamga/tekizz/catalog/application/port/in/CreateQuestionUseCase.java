package com.brandonkamga.tekizz.catalog.application.port.in;

import com.brandonkamga.tekizz.domain.Question;

public interface CreateQuestionUseCase {

    record CreateQuestionCommand(Long categoryId, String content, String explanation, String hint,
                                 String level, java.util.List<Long> tagIds) {}

    Question create(CreateQuestionCommand command);
}
