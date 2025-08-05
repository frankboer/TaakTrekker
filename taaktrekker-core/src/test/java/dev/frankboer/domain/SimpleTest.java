package dev.frankboer.domain;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.frankboer.service.*;
import dev.frankboer.service.jdbc.JdbcJobQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.concurrent.Executors;

@Disabled("Only for manual testing")
public class SimpleTest {
    private final DataSource dataSource = createPostgresDataSource();

    private JobQueue jobQueue;
    private Poller poller;
    private JobWorker jobWorker;

    @BeforeEach
    void setup() {
        var listener = new Listener() {
            @Override
            public void onJobScheduled(Job job) {}

            @Override
            public void onJobStarted(Job job) {}

            @Override
            public void onJobFinished(Job job) {}

            @Override
            public void onJobFailed(Job job) {}
        };
        jobQueue = new JdbcJobQueue(dataSource, listener);
        var executor = Executors.newVirtualThreadPerTaskExecutor();
        jobWorker = new SimpleJobWorker(executor);
        poller = new Poller(jobQueue, jobWorker, listener, 1000);
    }

    @Test
    void shouldProcess1000Tasks() throws InterruptedException {
        poller.start();
        for (var i = 0; i < 1000; i++) {
            jobQueue.enqueue(new ScheduleRequest("type" + i, 1, null));
        }

        Thread.sleep(10_000);
        poller.stop();
    }

    public static DataSource createPostgresDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/jobs");
        config.setUsername("postgres");
        config.setPassword("bier");

        return new HikariDataSource(config);
    }
}
