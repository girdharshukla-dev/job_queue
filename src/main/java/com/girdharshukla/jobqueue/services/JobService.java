package com.girdharshukla.jobqueue.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.girdharshukla.jobqueue.controllers.JobController.SubmitJobRequest;
import com.girdharshukla.jobqueue.models.Job;
import com.girdharshukla.jobqueue.models.Job.JobStatus;
import com.girdharshukla.jobqueue.repositories.JobRepository;

import io.lettuce.core.api.sync.RedisCommands;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final RedisCommands<String, String> commands;

    public JobService(JobRepository jobRepository, RedisCommands<String, String> commands){
        this.jobRepository = jobRepository;
        this.commands = commands;
    }

    public UUID submitJob(SubmitJobRequest request){
        UUID jobId = UUID.randomUUID();
        Job job = new Job();
        job.setId(jobId);
        job.setType(request.type());
        job.setPayload(request.payload());
        job.setStatus(JobStatus.QUEUED);

        jobRepository.insert(job);

        Long result = commands.lpush("jobs_queue", jobId.toString());
        System.out.println("lpush result in jobService: " + result);
        return jobId;
    }
}
