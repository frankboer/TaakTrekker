package dev.frankboer.service;

public class ScheduleException extends RuntimeException {
    public ScheduleException(Exception e) {
        super(e);
    }
}
