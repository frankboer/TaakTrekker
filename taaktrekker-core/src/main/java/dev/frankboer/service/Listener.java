package dev.frankboer.service;

import dev.frankboer.domain.Job;

public interface Listener {

    void onJobScheduled(Job job);

    void onJobStarted(Job job);

    void onJobFinished(Job job);

    void onJobFailed(Job job);
}
