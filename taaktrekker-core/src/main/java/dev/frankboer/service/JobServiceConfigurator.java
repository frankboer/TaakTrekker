package dev.frankboer.service;

import dev.frankboer.service.jdbc.JdbcJobRepository;

import javax.sql.DataSource;
import java.time.Duration;

/**
 * Builder for creating job processing system components.
 * Allows for flexible and fluent configuration of different parts of the system.
 */
public class JobServiceConfigurator {

    /**
     * Starts building a job processing system
     * @return A new builder instance
     */
    public static JobWorkerBuilder worker() {
        return new JobWorkerBuilder();
    }

    /**
     * Starts building just a job repository for scheduling jobs.
     * @return A new repository builder instance
     */
    public static RepositoryBuilder scheduler() {
        return new RepositoryBuilder();
    }

    /**
     * Builder for creating a complete job processing system
     */
    public static class JobWorkerBuilder {
        private DataSource dataSource;
        private Listener listener;
        private JobWorker jobWorker;
        private int maxParallelJobs = 1; // default value
        private Duration interval = Duration.ofMillis(100); // default value

        private JobWorkerBuilder() {}

        /**
         * Configure the data source
         */
        public JobWorkerBuilder withDataSource(DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        /**
         * Configure the job event listener
         */
        public JobWorkerBuilder withListener(Listener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * Configure a custom job worker
         */
        public JobWorkerBuilder withJobWorker(JobWorker jobWorker) {
            this.jobWorker = jobWorker;
            return this;
        }

        /**
         * Configure maximum number of parallel jobs
         */
        public JobWorkerBuilder withMaxParallelJobs(int maxParallelJobs) {
            this.maxParallelJobs = maxParallelJobs;
            return this;
        }

        /**
         * Configure the interval between polls.
         */
        public JobWorkerBuilder withInterval(Duration interval) {
            this.interval = interval;
            return this;
        }

        /**
         * Build the configured job processing system
         */
        public JobProcessingSystem build() {
            if (dataSource == null) {
                throw new IllegalStateException("DataSource must be configured");
            }

            // Create the job repository
            JobRepository jobRepository = new JdbcJobRepository(dataSource);

            // Create or use the provided job worker
            if (jobWorker == null) {
                throw new IllegalStateException("JobWorker must be configured");
            }

            // Create the poller
            Poller poller = new Poller(jobRepository, jobWorker, listener, interval, maxParallelJobs);

            return new JobProcessingSystem(jobRepository, jobWorker, poller);
        }
    }

    /**
     * Builder for creating just a job repository
     */
    public static class RepositoryBuilder {
        private DataSource dataSource;
        private Listener listener;

        private RepositoryBuilder() {}

        /**
         * Configure the data source
         */
        public RepositoryBuilder withDataSource(DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        /**
         * Build the configured job repository
         */
        public JobRepository build() {
            if (dataSource == null) {
                throw new IllegalStateException("DataSource must be configured");
            }

            return new JdbcJobRepository(dataSource);
        }
    }
}
