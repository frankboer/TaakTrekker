package dev.frankboer.service.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuration properties for TaakTrekker job processing system.
 */
@ConfigurationProperties(prefix = "taaktrekker")
public class TaakTrekkerProperties {

    /**
     * Whether to automatically start the job processing system.
     */
    private boolean autoStart = true;

    /**
     * Maximum number of parallel jobs to process.
     */
    private int maxParallelJobs = 10;

    /**
     * Interval between polling for new jobs.
     */
    private Duration pollingInterval = Duration.ofMillis(100);

    /**
     * Database schema name.
     */
    private String schema;

    /**
     * Database table name.
     */
    private String table;

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public int getMaxParallelJobs() {
        return maxParallelJobs;
    }

    public void setMaxParallelJobs(int maxParallelJobs) {
        this.maxParallelJobs = maxParallelJobs;
    }

    public Duration getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(Duration pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
