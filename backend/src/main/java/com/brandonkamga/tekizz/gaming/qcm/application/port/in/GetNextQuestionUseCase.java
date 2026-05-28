package com.brandonkamga.tekizz.gaming.qcm.application.port.in;

import com.brandonkamga.tekizz.dto.qcm.QcmQuestionResponse;

public interface GetNextQuestionUseCase {
    QcmQuestionResponse getNext(Long sessionId);
}
