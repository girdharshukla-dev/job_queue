package com.girdharshukla.jobqueue.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.girdharshukla.jobqueue.models.Job;
import com.girdharshukla.jobqueue.models.Job.JobStatus;
import com.girdharshukla.jobqueue.services.JobService;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService){
        this.jobService = jobService;
    }

    public record SubmitJobRequest(String type, JsonNode payload) {}
    public record SubmitJobResponse(UUID id) {}
    @PostMapping
    public ResponseEntity<SubmitJobResponse> submit(@RequestBody SubmitJobRequest submitJobRequest){
        UUID jobId = jobService.submitJob(submitJobRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SubmitJobResponse(jobId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Job> getJob(@PathVariable UUID id){
        Job job = jobService.getJobById(id);
        if(job == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(job);
    }

    @GetMapping
    public List<Job> getAllJobs(@PathVariable(required = false) String status){
        if(status == null){
            return jobService.listAllJobs();
        }
        return jobService.getJobByStatus(JobStatus.valueOf(status));
    }

}
