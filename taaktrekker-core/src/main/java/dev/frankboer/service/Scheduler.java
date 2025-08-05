package dev.frankboer.service;

import dev.frankboer.domain.ScheduleRequest;
import dev.frankboer.domain.ScheduleResponse;

public interface Scheduler {

    ScheduleResponse schedule(ScheduleRequest request);
}
