package dev.frankboer.domain;

import java.time.LocalDateTime;

public class Job {
    private Long id;
    private String name;
    private int priority;
    private JobStatus jobStatus;
    private String payload; // We'll store JSON as String
    private final LocalDateTime created;
    private LocalDateTime updated;
    private LocalDateTime started;
    private LocalDateTime finished;

    public Job(String name, int priority, String payload) {
        this.name = name;
        this.priority = priority;
        this.jobStatus = JobStatus.PENDING;
        this.payload = payload;
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public JobStatus getStatus() {
        return jobStatus;
    }

    public void setStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
        this.updated = LocalDateTime.now();
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public LocalDateTime getStarted() {
        return started;
    }

    public void setStarted(LocalDateTime started) {
        this.started = started;
    }

    public LocalDateTime getFinished() {
        return finished;
    }

    public void setFinished(LocalDateTime finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "Job{" + "id="
                + id + ", name='"
                + name + '\'' + ", priority="
                + priority + ", jobStatus="
                + jobStatus + ", payload='"
                + payload + '\'' + ", created="
                + created + ", updated="
                + updated + ", started="
                + started + ", finished="
                + finished + '}';
    }
}
