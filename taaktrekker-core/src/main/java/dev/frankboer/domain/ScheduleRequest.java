package dev.frankboer.domain;

public record ScheduleRequest(String type, int priority, String payload) {}
