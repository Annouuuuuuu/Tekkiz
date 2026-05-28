package com.brandonkamga.tekizz.gaming.qcm.application.port.in;

import com.brandonkamga.tekizz.dto.qcm.QcmLeaderboardResponse;

public interface GetLeaderboardUseCase {
    QcmLeaderboardResponse getLeaderboard(int page, int size, Long categoryId, String gameMode);
}
