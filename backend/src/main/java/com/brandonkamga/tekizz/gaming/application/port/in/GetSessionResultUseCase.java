package com.brandonkamga.tekizz.gaming.application.port.in;

import com.brandonkamga.tekizz.dto.qcm.QcmGameResultResponse;

public interface GetSessionResultUseCase {
    QcmGameResultResponse getResult(Long sessionId);
}
