package com.brandonkamga.tekizz.gaming.qcm.application.port.in;

import com.brandonkamga.tekizz.gaming.qcm.domain.model.vo.GameMode;
import com.brandonkamga.tekizz.dto.qcm.QcmGameSessionResponse;

import java.util.List;

public interface StartQcmSessionUseCase {
    QcmGameSessionResponse start(StartSessionCommand command);

    record StartSessionCommand(Long userId, Long categoryId, List<Long> tagIds,
                                GameMode gameMode, Integer lives) {}
}
