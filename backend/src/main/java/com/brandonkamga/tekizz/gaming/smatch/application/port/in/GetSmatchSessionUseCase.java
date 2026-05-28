package com.brandonkamga.tekizz.gaming.smatch.application.port.in;

import com.brandonkamga.tekizz.gaming.smatch.application.port.in.StartSmatchSessionUseCase.SmatchSessionView;

public interface GetSmatchSessionUseCase {
    SmatchSessionView getSession(Long sessionId);
}
