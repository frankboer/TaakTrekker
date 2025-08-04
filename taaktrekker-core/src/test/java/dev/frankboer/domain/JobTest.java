package dev.frankboer.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class JobTest {

    @Test
    void testJob() {
        Job job = new Job(null);
        assertNotNull(job);
    }

    @Test
    void testJobHasValidUuid() {
        Job job = new Job(UUID.randomUUID());
        assertNotNull(job.jobId());
        assertDoesNotThrow(() -> UUID.fromString(job.jobId().toString()));
    }
}
