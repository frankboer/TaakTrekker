package dev.frankboer.service.spring.example;

import dev.frankboer.domain.Job;
import dev.frankboer.service.JobWorker;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Sample implementation of JobWorker for demonstration purposes.
 */
@Component
public class SampleJobWorker implements JobWorker {
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public CompletionStage<Job> run(Job job) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return processJob(job);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }

    private Job processJob(Job job) throws Exception {
        // Simulate some work
        Thread.sleep(100);

        // Access job data
        String type = job.getName();
        String data = job.getPayload();

        // Log processing information
        System.out.println("Processing job: " + job.getId());
        System.out.println("Job type: " + type);
        System.out.println("Job data: " + data);

        return job;
    }
}
