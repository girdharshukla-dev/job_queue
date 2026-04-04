package com.girdharshukla.jobqueue.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.girdharshukla.jobqueue.models.Job;
import com.girdharshukla.jobqueue.models.Job.JobStatus;

@Repository
public class JobRepository implements RowMapper<Job> {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Job mapRow(ResultSet rs, int rowNum) throws SQLException {
        Job job = new Job();
        job.setId(UUID.fromString(rs.getString("id")));
        job.setType(rs.getString("type"));
        try {
            job.setPayload(objectMapper.readTree(rs.getString("payload")));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        job.setStatus(JobStatus.valueOf(rs.getString("status")));
        job.setAttempt(rs.getInt("attempt"));
        job.setMaxAttempts(rs.getInt("max_attempts"));
        job.setCreatedAt(rs.getTimestamp("created_at"));
        job.setStartedAt(rs.getTimestamp("started_at"));
        job.setFinishedAt(rs.getTimestamp("finished_at"));
        String result = rs.getString("result");
        if (result != null) {
            try {
                job.setResult(objectMapper.readTree(rs.getString("result")));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        job.setError(rs.getString("error"));

        return job;
    }

    public JobRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(Job job) {

        if (job.getMaxAttempts() == null) {
            String sql = "INSERT INTO jobs (id, type, payload, status) VALUES (?, ?, ?::jsonb, ?)";
            jdbcTemplate.update(sql, job.getId(), job.getType(), job.getPayload().toString(), job.getStatus().name());
        } else {
            String sql = "INSERT INTO jobs (id, type, payload, status, max_attempts) VALUES (?, ?, ?::jsonb, ?, ?)";
            jdbcTemplate.update(sql, job.getId(), job.getType(), job.getPayload().toString(), job.getStatus().name(), job.getMaxAttempts());

        }

    }

    public Job findById(UUID id) {
        try {
            String sql = "SELECT * FROM jobs WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, this, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void transition(UUID id, JobStatus status, String error) {
        switch (status) {
            case RUNNING ->
                jdbcTemplate.update(
                        "UPDATE jobs SET status = 'RUNNING', started_at = NOW(), attempt = attempt + 1 WHERE id = ?",
                        id);

            case SUCCESS ->
                jdbcTemplate.update("UPDATE jobs SET status = 'SUCCESS', finished_at = NOW() WHERE id = ?", id);

            case FAILED ->
                jdbcTemplate.update("UPDATE jobs SET status = 'FAILED', error = ?, finished_at = NOW() WHERE id = ?",
                        error, id);

            case QUEUED ->
                jdbcTemplate.update("UPDATE jobs SET status = 'QUEUED' WHERE id = ?", id);
        }
    }

    public List<Job> findByStatus(JobStatus status) {
        String sql = "SELECT * FROM jobs WHERE status = ?";
        return jdbcTemplate.query(sql, this, status.name());
    }

    public List<Job> getAllJobs() {
        return jdbcTemplate.query("SELECT * FROM jobs", this);
    }

    public List<Job> recoverJobs() {
        jdbcTemplate.update("UPDATE jobs SET status = 'QUEUED' WHERE status = 'RUNNING'");
        return jdbcTemplate.query("SELECT * FROM jobs WHERE status = 'QUEUED'", this);
    }
}
