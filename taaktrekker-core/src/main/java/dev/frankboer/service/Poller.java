package dev.frankboer.service;

import dev.frankboer.domain.Job;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Poller {
    private final JobQueue jobQueue;
    private final JobWorker jobWorker;
    private final ScheduledExecutorService scheduler;
    private final ExecutorService jobExecutor;
    private final AtomicInteger slots = new AtomicInteger(0);
    private final Integer maxSlots;
    private volatile boolean running;

    public Poller(JobQueue jobQueue, JobWorker jobWorker, int maxParallelJobs) {
        this.jobQueue = jobQueue;
        this.jobWorker = jobWorker;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.jobExecutor = Executors.newFixedThreadPool(maxParallelJobs);
        this.maxSlots = maxParallelJobs;
    }

    public void start() {
        running = true;
        scheduler.scheduleWithFixedDelay(this::pollJobQueue, 0, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        running = false;
        scheduler.shutdown();
        jobExecutor.shutdown();
    }

    private void pollJobQueue() {
        if (!running) return;

        if (slots.get() < maxSlots) {
            jobQueue.dequeue().ifPresent(job -> {
                jobExecutor.submit(() -> {
                    slots.incrementAndGet();
                    try {
                        jobWorker.run(job)
                                .whenComplete((result, error) -> {
                                    if (error != null) {
                                        jobQueue.updateStatus(job, Job.Status.FAILED);
                                    } else {
                                        jobQueue.updateStatus(job, Job.Status.COMPLETED);
                                    }
                                    slots.decrementAndGet();
                                });
                    } catch (Exception e) {
                        jobQueue.updateStatus(job, Job.Status.FAILED);
                        slots.decrementAndGet();
                    }
                });
            });
        }
    }
}
