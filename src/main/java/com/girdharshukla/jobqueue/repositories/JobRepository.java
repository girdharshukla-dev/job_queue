package com.girdharshukla.jobqueue.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.girdharshukla.jobqueue.models.Job;

@Repository
public class JobRepository {
    private final JdbcTemplate jdbcTemplate;

    public JobRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(Job job){
        String sql = "INSERT INTO jobs (id, type, payload, status, max_attempts) VALUES (?, ?, ?::jsonb, ?, ?)";
        jdbcTemplate.update(sql, job.getId(), job.getType(), job.getPayload().toString(), job.getStatus().name(), job.getMaxAttempt());        
    }
}
