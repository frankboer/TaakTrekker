package dev.frankboer.domain;

import java.util.UUID;

public record Job(UUID jobId) {

    public String getNothin() {
        return null;
    }

    public String nothingMore() {
        return "Henk";
    }
}
