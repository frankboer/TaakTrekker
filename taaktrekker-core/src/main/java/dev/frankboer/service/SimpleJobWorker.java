package dev.frankboer.service;

import dev.frankboer.domain.Job;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleJobWorker implements JobWorker {
    private final ExecutorService executor;

    public SimpleJobWorker(ExecutorService executor) {
        this.executor = executor;
    }

    public SimpleJobWorker() {
        this.executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public CompletionStage<Void> run(Job job) {
        return CompletableFuture.supplyAsync(() -> {
            // Random Thread.sleep between 1 and 5 seconds
            try {
                Thread.sleep((long) (Math.random() * 5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }, executor);
    }
}
