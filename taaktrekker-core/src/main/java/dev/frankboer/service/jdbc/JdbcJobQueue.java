package dev.frankboer.service.jdbc;

import dev.frankboer.domain.Job;
import dev.frankboer.domain.ScheduleRequest;
import dev.frankboer.service.JobQueue;
import dev.frankboer.service.ScheduleException;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class JdbcJobQueue implements JobQueue {
    private final DataSource dataSource;

    public JdbcJobQueue(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Job enqueue(ScheduleRequest request) {
        try (var connection = dataSource.getConnection()) {
            String sql = "INSERT INTO jobs (type, priority, payload) VALUES (?, ?, ?::jsonb) RETURNING *";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, request.type());
                stmt.setInt(2, request.priority());
                stmt.setString(3, request.payload());

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return mapResultSetToJob(rs);
                }
            }

        } catch (SQLException e) {
            throw new ScheduleException(e);
        }

        throw new ScheduleException(new IllegalStateException("No job returned"));
    }

    @Override
    public Optional<Job> dequeue() {
        try(var connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                String sql = """
                UPDATE jobs 
                SET status = 'processing', updated_at = NOW() 
                WHERE id = (
                    SELECT id FROM jobs 
                    WHERE status = 'pending' 
                    ORDER BY priority DESC, created_at ASC 
                    LIMIT 1
                    FOR UPDATE SKIP LOCKED
                )
                RETURNING *
                """;
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        Job job = mapResultSetToJob(rs);
                        connection.commit();
                        return Optional.of(job);
                    }
                }
                connection.commit();
                return Optional.empty();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
    } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateStatus(Job job, Job.Status status) {
        try (var connection = dataSource.getConnection()) {
            String sql = "UPDATE jobs SET status = ?, updated_at = NOW() WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, status.name());
                stmt.setLong(2, job.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private Job mapResultSetToJob(ResultSet rs) throws SQLException {
        Job job = new Job(
                rs.getString("type"),
                rs.getInt("priority"),
                rs.getString("payload")
        );
        job.setId(rs.getLong("id"));
        job.setStatus(Job.Status.valueOf(rs.getString("status")));
        return job;
    }

}
