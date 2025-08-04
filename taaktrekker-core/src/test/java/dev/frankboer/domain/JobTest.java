package dev.frankboer.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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