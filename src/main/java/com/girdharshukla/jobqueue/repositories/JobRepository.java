package com.girdharshukla.jobqueue.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

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
        Job jobb = new Job();
        jobb.setId(UUID.fromString(rs.getString("id")));
        jobb.setType(rs.getString("type"));
        try {
            jobb.setPayload(objectMapper.readTree(rs.getString("payload")));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        jobb.setStatus(JobStatus.valueOf(rs.getString("status")));
        jobb.setAttempt(rs.getInt("attempt"));
        jobb.setMaxAttempts(rs.getInt("max_attempts"));
        jobb.setCreatedAt(rs.getTimestamp("created_at"));
        jobb.setStartedAt(rs.getTimestamp("started_at"));
        jobb.setFinishedAt(rs.getTimestamp("finished_at"));
        try {
            jobb.setResult(objectMapper.readTree(rs.getString("result")));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        jobb.setError(rs.getString("error"));

        return jobb;
    }

    public JobRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(Job job) {
        String sql = "INSERT INTO jobs (id, type, payload, status, max_attempts) VALUES (?, ?, ?::jsonb, ?, ?)";
        jdbcTemplate.update(sql, job.getId(), job.getType(), job.getPayload().toString(), job.getStatus().name(),
                job.getMaxAttempts());
    }

    public Job findById(UUID id) {
        String sql = "SELECT * FROM jobs WHERE id = ?";
        Job job = jdbcTemplate.queryForObject(sql, this, id);
        return job;
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

    public List<Job> findByStatus(JobStatus status){
        String sql = "SELECT * FROM jobs WHERE status = ?";
        return jdbcTemplate.query(sql, this, status.name());
    }

    public List<Job> getAllJobs(){
        return jdbcTemplate.query("SELECT * FROM jobs", this);
    }
}
