package dev.frankboer.service.impl;

import dev.frankboer.domain.ScheduleRequest;
import dev.frankboer.domain.ScheduleResponse;
import dev.frankboer.service.ScheduleException;
import dev.frankboer.service.Scheduler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class JdbcScheduler implements Scheduler {
    private final DataSource dataSource;

    public JdbcScheduler(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ScheduleResponse schedule(ScheduleRequest request) {
        try (Connection connection = dataSource.getConnection()) {
            // TODO implement
        } catch (SQLException e) {
            throw new ScheduleException(e);
        }

        return new ScheduleResponse();
    }
}
