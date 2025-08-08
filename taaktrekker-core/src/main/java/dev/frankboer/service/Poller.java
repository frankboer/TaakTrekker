package dev.frankboer.service;

import dev.frankboer.domain.Job;
import dev.frankboer.domain.JobStatus;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Poller {
    private final JobRepository jobRepository;
    private final JobWorker jobWorker;
    private final Listener listener;
    private final Duration interval;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final AtomicInteger slots = new AtomicInteger(0);
    private final Integer maxSlots;
    private volatile boolean running;

    public Poller(
            JobRepository jobRepository, JobWorker jobWorker, Listener listener, Duration interval, int parallelJobs) {
        this.jobRepository = jobRepository;
        this.jobWorker = jobWorker;
        this.listener = listener;
        this.interval = interval;
        this.maxSlots = parallelJobs;
    }

    public void start() {
        running = true;
        scheduler.scheduleWithFixedDelay(this::pollJobQueue, 0, interval.toMillis(), TimeUnit.MILLISECONDS);
    }

    public void stop() {
        running = false;
        scheduler.shutdown();
    }

    private void pollJobQueue() {
        if (!running) return;

        if (slots.get() < maxSlots) {
            jobRepository.dequeue(maxSlots - slots.get()).forEach(job -> {
                listener.onJobStarted(job);
                slots.incrementAndGet();
                try {
                    jobWorker.run(job).whenComplete((result, error) -> handleResult(job, error));
                } catch (Exception e) {
                    listener.onJobFailed(job);
                    jobRepository.updateStatus(job, JobStatus.FAILED);
                    slots.decrementAndGet();
                }
            });
        }
    }

    private void handleResult(Job job, Throwable error) {
        try {
            slots.decrementAndGet();
            if (error != null) {
                jobRepository.updateStatus(job, JobStatus.FAILED);
                listener.onJobFailed(job);
            } else {
                jobRepository.updateStatus(job, JobStatus.COMPLETED);
                listener.onJobFinished(job);
            }
        } catch (Exception e) {
            System.out.println("Something went wrong:" + e);
            slots.decrementAndGet();
        }
    }
}
