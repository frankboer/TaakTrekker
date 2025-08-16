package dev.frankboer.service.spring.example;

import dev.frankboer.domain.ScheduleRequest;
import dev.frankboer.service.JobRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Sample Spring Boot application demonstrating TaakTrekker integration.
 */
@SpringBootApplication
public class TaakTrekkerExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaakTrekkerExampleApplication.class, args);
    }

    /**
     * Example of scheduling jobs using the JobRepository.
     */
    @Bean
    public CommandLineRunner commandLineRunner(JobRepository jobRepository) {
        return args -> {
            // Schedule a job for immediate execution
            jobRepository.enqueue(List.of(new ScheduleRequest("DIRECT", 0, "{\"message\": \"Hello, World!\"}")));
            System.out.println("Jobs scheduled. Application will process them automatically.");
        };
    }
}
