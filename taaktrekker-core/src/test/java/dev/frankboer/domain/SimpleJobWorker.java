package dev.frankboer.domain;

import dev.frankboer.service.JobWorker;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;

public class SimpleJobWorker implements JobWorker {
    private final ExecutorService executor;

    public SimpleJobWorker(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public CompletionStage<Void> run(Job job) {
        return CompletableFuture.supplyAsync(
                () -> {
                    // Random Thread.sleep between 1 and 5 seconds
                    try {
                        System.out.println("job = " + job);
                        Thread.sleep((long) (Math.random() * 5000));
                    } catch (InterruptedException e) {
                        throw new IllegalStateException(e);
                    }
                    return null;
                },
                executor);
    }
}
