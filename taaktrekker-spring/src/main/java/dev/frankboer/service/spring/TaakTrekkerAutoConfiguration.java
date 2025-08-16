package dev.frankboer.service.spring;

import dev.frankboer.service.JobProcessingSystem;
import dev.frankboer.service.JobRepository;
import dev.frankboer.service.JobServiceConfigurator;
import dev.frankboer.service.JobWorker;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;

/**
 * Spring Boot auto-configuration for TaakTrekker job processing system.
 * Automatically configures the job processing system based on properties and available beans.
 */
@AutoConfiguration
@ConditionalOnClass(JobProcessingSystem.class)
@EnableConfigurationProperties(TaakTrekkerProperties.class)
@Import(TaakTrekkerConfiguration.class)
public class TaakTrekkerAutoConfiguration {

    /**
     * Configures a JobRepository when DataSource is available.
     */
    @Configuration
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnMissingBean(JobRepository.class)
    static class JobRepositoryConfiguration {

        @Bean
        public JobRepository jobRepository(DataSource dataSource, TaakTrekkerProperties properties) {
            return JobServiceConfigurator.scheduler()
                    .withDataSource(dataSource)
                    .withSchema(properties.getSchema())
                    .withTable(properties.getTable())
                    .build();
        }
    }

    /**
     * Configures a JobProcessingSystem when all required components are available.
     */
    @Configuration
    @ConditionalOnBean({DataSource.class, JobWorker.class})
    @ConditionalOnMissingBean(JobProcessingSystem.class)
    static class JobProcessingSystemConfiguration {

        @Bean
        public JobProcessingSystem jobProcessingSystem(
                DataSource dataSource,
                JobWorker jobWorker,
                TaakTrekkerProperties properties) {

            return JobServiceConfigurator.worker()
                    .withDataSource(dataSource)
                    .withJobWorker(jobWorker)
                    .withMaxParallelJobs(properties.getMaxParallelJobs())
                    .withInterval(properties.getPollingInterval())
                    .withSchema(properties.getSchema())
                    .withTable(properties.getTable())
                    .build();
        }
    }

    /**
     * Configuration for starting the job processing system automatically.
     */
    @Configuration
    @ConditionalOnProperty(prefix = "taaktrekker", name = "auto-start", havingValue = "true", matchIfMissing = true)
    static class AutoStartConfiguration {

        @Bean
        public JobProcessingSystemLifecycle jobProcessingSystemLifecycle(JobProcessingSystem jobProcessingSystem) {
            return new JobProcessingSystemLifecycle(jobProcessingSystem);
        }
    }
}
