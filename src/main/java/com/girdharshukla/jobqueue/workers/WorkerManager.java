package com.girdharshukla.jobqueue.workers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;

import org.springframework.stereotype.Component;

import com.girdharshukla.jobqueue.models.Job;
import com.girdharshukla.jobqueue.repositories.JobRepository;

import io.lettuce.core.api.sync.RedisCommands;

@Component
public class WorkerManager {
    private final JobRepository jobRepository; 
    private final RedisCommands<String, String> commands;   

    public WorkerManager(JobRepository jobRepository, RedisCommands<String, String> commands, Worker worker){
        this.commands = commands;
        this.jobRepository = jobRepository;
        jobsRecovery();

        ExecutorService exe = Executors.newFixedThreadPool(8);
        for(int i = 0; i < 8; i++){
            exe.submit(worker);
        }
    }

    private void jobsRecovery(){
        List<Job> jobs = jobRepository.recoverJobs();
        for(Job job : jobs){
            commands.lpush("jobs_queue", job.getId().toString());
        }
    }

}
