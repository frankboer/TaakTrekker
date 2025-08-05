package dev.frankboer.service;

import dev.frankboer.domain.Job;
import dev.frankboer.domain.ScheduleRequest;
import java.util.Optional;

public interface JobQueue {

    Job enqueue(ScheduleRequest request);

    Optional<Job> dequeue();

    void updateStatus(Job job, Job.Status status);
}
