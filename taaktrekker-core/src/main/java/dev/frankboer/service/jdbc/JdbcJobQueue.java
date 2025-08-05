package dev.frankboer.service.jdbc;

import dev.frankboer.domain.Job;
import dev.frankboer.domain.ScheduleRequest;
import dev.frankboer.service.JobQueue;
import dev.frankboer.service.Listener;
import dev.frankboer.service.ScheduleException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import javax.sql.DataSource;

public class JdbcJobQueue implements JobQueue {
    private final DataSource dataSource;
    private final Listener listener;

    public JdbcJobQueue(DataSource dataSource, Listener listener) {
        this.dataSource = dataSource;
        this.listener = listener;
    }

    @Override
    public Job enqueue(ScheduleRequest request) {
        try (var connection = dataSource.getConnection()) {
            var sql = "INSERT INTO jobs (type, priority, payload) VALUES (?, ?, ?::jsonb) RETURNING *";
            try (var stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, request.type());
                stmt.setInt(2, request.priority());
                stmt.setString(3, request.payload());

                var rs = stmt.executeQuery();
                if (rs.next()) {
                    Job job = mapResultSetToJob(rs);
                    listener.onJobScheduled(job);
                    return job;
                }
            }

        } catch (SQLException e) {
            throw new ScheduleException(e);
        }

        throw new ScheduleException(new IllegalStateException("No job returned"));
    }

    @Override
    public List<Job> dequeue(int limit) {
        try (var connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                String sql =
                        """
                UPDATE jobs
                SET status = 'RUNNING', updated_at = NOW()
                WHERE id IN (
                    SELECT id FROM jobs
                    WHERE status = 'PENDING'
                    ORDER BY priority DESC, created_at ASC
                    LIMIT %d
                    FOR UPDATE SKIP LOCKED
                )
                RETURNING *
                """
                                .formatted(limit);
                try (var stmt = connection.prepareStatement(sql)) {
                    var rs = stmt.executeQuery();

                    var jobs = new LinkedList<Job>();
                    while (rs.next()) {
                        Job job = mapResultSetToJob(rs);
                        jobs.add(job);
                    }
                    connection.commit();
                    return jobs;
                }
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
            var sql = "UPDATE jobs SET status = ?, updated_at = NOW() WHERE id = ?";
            try (var stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, status.name());
                stmt.setLong(2, job.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Job mapResultSetToJob(ResultSet rs) throws SQLException {
        Job job = new Job(rs.getString("type"), rs.getInt("priority"), rs.getString("payload"));
        job.setId(rs.getLong("id"));
        job.setStatus(Job.Status.valueOf(rs.getString("status")));
        return job;
    }
}
