package dev.frankboer.domain;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.frankboer.service.JobProcessingSystem;
import dev.frankboer.service.JobServiceConfigurator;
import dev.frankboer.service.Listener;
import dev.frankboer.service.Poller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

 @Disabled("Only for manual testing")
public class SimpleTest {
    private static final int MAX_PARALLEL_JOBS = 2000;
    private JobProcessingSystem configurator;
    private Poller poller;
    private TestListener listener;

    @BeforeEach
    void setup() {
        listener = new TestListener();
        configurator = JobServiceConfigurator.worker()
                .withDataSource(createPostgresDataSource())
                .withListener(listener)
                .withJobWorker(new SimpleJobWorker(Executors.newVirtualThreadPerTaskExecutor()))
                .withInterval(Duration.ofMillis(200))
                .withMaxParallelJobs(MAX_PARALLEL_JOBS)
                .build();

        poller = configurator.getPoller();
    }

    @Test
    void shouldProcess1000Tasks() {

        configurator.start();
        var list = new ArrayList<ScheduleRequest>();
        for (var i = 0; i < 10_000; i++) {
            ScheduleRequest scheduleRequest = new ScheduleRequest(String.valueOf(i), 1, null);
            var job = new Job(scheduleRequest.type(), scheduleRequest.priority(), scheduleRequest.payload());
            listener.onJobScheduled(job);
            list.add(scheduleRequest);
        }
        configurator.getJobRepository().enqueue(list);

        listener.waitForCompletion().join();
        configurator.stop();
    }

    private static class TestListener implements Listener {
        Map<String, CompletableFuture<Job>> map = new HashMap<>();

        public void onJobScheduled(Job job) {
            CompletableFuture<Job> future = new CompletableFuture<>();
            map.put(job.getName(), future);
        }

        @Override
        public void onJobStarted(Job job) {}

        @Override
        public void onJobFinished(Job job) {
            //            System.out.println(job);
            map.getOrDefault(job.getName(), new CompletableFuture<>()).complete(job);
        }

        @Override
        public void onJobFailed(Job job) {
            throw new IllegalStateException("Job failed");
        }

        public CompletableFuture<Void> waitForCompletion() {
            return CompletableFuture.allOf(map.values().toArray(new CompletableFuture[0]));
        }
    }

    public static DataSource createPostgresDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/jobs");
        config.setUsername("postgres");
        config.setPassword("bier");

        return new HikariDataSource(config);
    }
}
