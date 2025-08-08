package dev.frankboer.service;

/**
 * Container for all components of a job processing system.
 */
public class JobProcessingSystem {
    private final JobRepository jobRepository;
    private final JobWorker jobWorker;
    private final Poller poller;

    JobProcessingSystem(JobRepository jobRepository, JobWorker jobWorker, Poller poller) {
        this.jobRepository = jobRepository;
        this.jobWorker = jobWorker;
        this.poller = poller;
    }

    /**
     * Starts the job processing system.
     */
    public void start() {
        poller.start();
    }

    /**
     * Stops the job processing system.
     */
    public void stop() {
        poller.stop();
    }

    public JobRepository getJobRepository() {
        return jobRepository;
    }

    public JobWorker getJobWorker() {
        return jobWorker;
    }

    public Poller getPoller() {
        return poller;
    }
}
