package dev.frankboer.service;

import dev.frankboer.domain.Job;
import dev.frankboer.domain.JobStatus;
import dev.frankboer.domain.ScheduleRequest;
import java.util.List;

public interface JobRepository {

    void enqueue(List<ScheduleRequest> request);

    List<Job> dequeue(int limit);

    void updateStatus(Job job, JobStatus jobStatus);
}
