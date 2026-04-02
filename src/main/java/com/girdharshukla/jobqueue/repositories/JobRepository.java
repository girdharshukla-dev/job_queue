package com.girdharshukla.jobqueue.repositories;

import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.girdharshukla.jobqueue.models.Job;
import com.girdharshukla.jobqueue.models.Job.JobStatus;

@Repository
public class JobRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JobRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(Job job){
        String sql = "INSERT INTO jobs (id, type, payload, status, max_attempts) VALUES (?, ?, ?::jsonb, ?, ?)";
        jdbcTemplate.update(sql, job.getId(), job.getType(), job.getPayload().toString(), job.getStatus().name(), job.getMaxAttempt());        
    }
    
    public Job findById(UUID id){
        String sql = "SELECT * FROM jobs WHERE id = ?";
        Job job = jdbcTemplate.queryForObject(sql, (rs, rowNum)->{
            Job jobb = new Job();
            jobb.setId(UUID.fromString(rs.getString("id")));
            jobb.setType(rs.getString("type"));
            try {
                jobb.setPayload(objectMapper.readTree(rs.getString("payload")));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            jobb.setAttempt(rs.getInt("attempt"));
            jobb.setMaxAttempt(rs.getInt("max_attempt"));
            jobb.setStatus(JobStatus.valueOf(rs.getString("status")));

            return jobb;
        });

        return job;
    }

    public void transition(UUID id, JobStatus status, String error){
        switch(status){
            case RUNNING -> 
                jdbcTemplate.update("UPDATE jobs SET status = 'RUNNING', started_at = NOW(), attempt = attempt + 1 WHERE id = ?", id);
            
            case SUCCESS ->
                jdbcTemplate.update("UPDATE jobs SET status = 'SUCCESS', finished_at = NOW() WHERE id = ?", id);
            
            case FAILED ->
                jdbcTemplate.update("UPDATE jobs SET status = 'FAILED', error = ?,finished_at = NOW() WHERE id = ?", id);
        }
    }
}
