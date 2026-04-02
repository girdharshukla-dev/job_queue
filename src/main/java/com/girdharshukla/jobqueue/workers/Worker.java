package com.girdharshukla.jobqueue.workers;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.girdharshukla.jobqueue.models.Job;
import com.girdharshukla.jobqueue.models.Job.JobStatus;
import com.girdharshukla.jobqueue.repositories.JobRepository;

import io.lettuce.core.KeyValue;
import io.lettuce.core.api.sync.RedisCommands;

@Component
public class Worker implements Runnable{
    private final RedisCommands<String, String> commands;
    private final JobRepository jobRepository;

    public Worker(RedisCommands<String, String> commands, JobRepository jobRepository){
        this.commands = commands;
        this.jobRepository = jobRepository;
    }

    @Override
    public void run(){
        while(true){
            KeyValue<String, String> ans = commands.brpop(0, "job_queue");
            String jobId = ans.getValue();
            UUID id = UUID.fromString(jobId);
            Job job = jobRepository.findById(id);
            jobRepository.transition(id, JobStatus.RUNNING, null);
            try{
                jobRepository.transition(id, JobStatus.SUCCESS, null);
            }catch(Exception e){
                jobRepository.transition(id, JobStatus.FAILED, e.getMessage());
            }
            
        }
    }
}
