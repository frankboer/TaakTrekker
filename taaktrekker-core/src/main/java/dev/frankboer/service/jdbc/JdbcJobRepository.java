package dev.frankboer.service.jdbc;

import dev.frankboer.domain.Job;
import dev.frankboer.domain.JobStatus;
import dev.frankboer.domain.ScheduleRequest;
import dev.frankboer.service.JobRepository;
import dev.frankboer.service.ScheduleException;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class JdbcJobRepository implements JobRepository {
    private static final String SCHEMA_TABLE_PATTERN = "^[a-zA-Z_][a-zA-Z0-9_]{0,62}\\.[a-zA-Z_][a-zA-Z0-9_]{0,62}$";

    private final DataSource dataSource;
    private final String identifier;

    public JdbcJobRepository(DataSource dataSource, String schemaName, String tableName) {
        this.dataSource = dataSource;
        this.identifier = "%s.%s".formatted(schemaName,tableName);
        isValidSchemaTable(identifier);
    }

    @Override
    public void enqueue(List<ScheduleRequest> requests) {
        try (var connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            var sql = "INSERT INTO %s (type, priority, payload) VALUES (?, ?, ?::jsonb)".formatted(identifier);
            try (var stmt = connection.prepareStatement(sql)) {
                for (ScheduleRequest request : requests) {
                    stmt.setString(1, request.type());
                    stmt.setInt(2, request.priority());
                    stmt.setString(3, request.payload());

                    stmt.addBatch();
                }
                stmt.executeBatch();
                connection.commit();
            }
        } catch (SQLException e) {
            throw new ScheduleException(e);
        }
    }

    @Override
    public List<Job> dequeue(int limit) {
        try (var connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                String sql =
                        """
                UPDATE %s
                SET status = 'RUNNING', updated_at = NOW()
                WHERE id IN (
                    SELECT id FROM %s
                    WHERE status = 'PENDING'
                    ORDER BY priority DESC, created_at ASC
                    LIMIT %d
                    FOR UPDATE SKIP LOCKED
                )
                RETURNING *
                """
                                .formatted(identifier, identifier, limit);
                try (var stmt = connection.prepareStatement(sql)) {
                    var rs = stmt.executeQuery();

                    var jobs = new LinkedList<Job>();
                    while (rs.next()) {
                        Job job = mapResultSetToJob(rs);
                        jobs.add(job);
                    }
                    connection.commit();
                    System.out.println("jobs = " + jobs.size());
                    return jobs;
                }
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void updateStatus(Job job, JobStatus status) {
        try (var connection = dataSource.getConnection()) {
            var sql = "UPDATE %s SET status = ?, updated_at = NOW() WHERE id = ?".formatted(identifier);
            try (var stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, status.name());
                stmt.setLong(2, job.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isValidSchemaTable(String input) {
        if (input == null || input.isBlank()) {
            return false;
        }
        return input.matches(SCHEMA_TABLE_PATTERN);
    }

    private Job mapResultSetToJob(ResultSet rs) throws SQLException {
        Job job = new Job(rs.getString("type"), rs.getInt("priority"), rs.getString("payload"));
        job.setId(rs.getLong("id"));
        job.setStatus(JobStatus.valueOf(rs.getString("status")));
        return job;
    }
}
