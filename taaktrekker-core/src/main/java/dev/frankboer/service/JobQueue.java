package dev.frankboer.service;

import dev.frankboer.domain.Job;
import dev.frankboer.domain.ScheduleRequest;
import java.util.List;

public interface JobQueue {

    Job enqueue(ScheduleRequest request);

    List<Job> dequeue(int limit);

    void updateStatus(Job job, Job.Status status);
}
