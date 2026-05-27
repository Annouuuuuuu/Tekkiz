package com.brandonkamga.tekizz.gaming.qcm.application.port.in;

import com.brandonkamga.tekizz.gaming.qcm.domain.model.QcmQuestion;

import java.util.List;

public interface CreateQcmQuestionUseCase {

    record CreateQcmQuestionCommand(Long categoryId, String content, String explanation, String hint,
                                    String level, List<Long> tagIds) {}

    QcmQuestion create(CreateQcmQuestionCommand command);
}
