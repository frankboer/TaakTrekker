package dev.frankboer.service;

import dev.frankboer.domain.Job;

import java.util.concurrent.CompletionStage;

public interface JobWorker {

    CompletionStage<Void> run(Job job);
}
