package com.brandonkamga.tekizz.gaming.application.port.in;

import com.brandonkamga.tekizz.dto.qcm.QcmSubmitAnswerRequest;
import com.brandonkamga.tekizz.dto.qcm.QcmSubmitAnswerResponse;

public interface SubmitAnswerUseCase {
    QcmSubmitAnswerResponse submit(Long sessionId, QcmSubmitAnswerRequest request);
}
