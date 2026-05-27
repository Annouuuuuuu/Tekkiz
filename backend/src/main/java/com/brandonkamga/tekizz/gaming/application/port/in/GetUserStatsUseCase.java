package com.brandonkamga.tekizz.gaming.application.port.in;

import com.brandonkamga.tekizz.dto.qcm.QcmUserStatsResponse;

public interface GetUserStatsUseCase {
    QcmUserStatsResponse getStats(Long userId);
}
