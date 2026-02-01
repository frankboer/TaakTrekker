package dev.frankboer.service.spring;

import dev.frankboer.service.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Manual configuration class for TaakTrekker in a Spring application.
 * This can be used when auto-configuration is not sufficient or you need more control.
 */
@Configuration
@EnableConfigurationProperties(TaakTrekkerProperties.class)
public class TaakTrekkerConfiguration {

    /**
     * Creates a JobRepository for scheduling jobs without processing them.
     * 
     * @param dataSource The datasource to use
     * @param properties TaakTrekker configuration properties
     * @return A configured JobRepository
     */
    @Bean
    public JobRepository jobRepository(DataSource dataSource, TaakTrekkerProperties properties) {
        return JobServiceConfigurator.scheduler()
                .withDataSource(dataSource)
                .withSchema(properties.getSchema())
                .withTable(properties.getTable())
                .build();
    }

    /**
     * Creates a complete JobProcessingSystem with all required components.
     * 
     * @param dataSource The datasource to use
     * @param jobWorker The job worker implementation
     * @param listener An optional listener for job events (can be null)
     * @param properties TaakTrekker configuration properties
     * @return A fully configured JobProcessingSystem
     */
    @Bean
    public JobProcessingSystem jobProcessingSystem(
            DataSource dataSource,
            JobWorker jobWorker,
            Listener listener,
            TaakTrekkerProperties properties) {

        JobServiceConfigurator.JobWorkerBuilder builder = JobServiceConfigurator.worker()
                .withDataSource(dataSource)
                .withJobWorker(jobWorker)
                .withMaxParallelJobs(properties.getMaxParallelJobs())
                .withInterval(properties.getPollingInterval())
                .withSchema(properties.getSchema())
                .withTable(properties.getTable());

        if (listener != null) {
            builder.withListener(listener);
        }

        return builder.build();
    }

    /**
     * Creates a lifecycle manager for the JobProcessingSystem that
     * automatically starts and stops it with the Spring application.
     * 
     * @param jobProcessingSystem The system to manage
     * @return A lifecycle manager bean
     */
    @Bean
    public JobProcessingSystemLifecycle jobProcessingSystemLifecycle(JobProcessingSystem jobProcessingSystem) {
        return new JobProcessingSystemLifecycle(jobProcessingSystem);
    }
}
