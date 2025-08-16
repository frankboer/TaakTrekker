package dev.frankboer.service.spring;

import dev.frankboer.service.JobProcessingSystem;
import org.springframework.context.SmartLifecycle;

/**
 * Spring lifecycle integration for JobProcessingSystem.
 * Ensures that the system is started and stopped with the Spring application.
 */
public class JobProcessingSystemLifecycle implements SmartLifecycle {

    private final JobProcessingSystem jobProcessingSystem;
    private boolean running = false;

    public JobProcessingSystemLifecycle(JobProcessingSystem jobProcessingSystem) {
        this.jobProcessingSystem = jobProcessingSystem;
    }

    @Override
    public void start() {
        if (!running) {
            jobProcessingSystem.start();
            running = true;
        }
    }

    @Override
    public void stop() {
        if (running) {
            jobProcessingSystem.stop();
            running = false;
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public int getPhase() {
        return 0;
    }
}
