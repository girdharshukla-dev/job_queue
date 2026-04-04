package com.girdharshukla.jobqueue.workers;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.girdharshukla.jobqueue.jobHandlers.JobHandler;
import com.girdharshukla.jobqueue.models.Job;
import com.girdharshukla.jobqueue.models.Job.JobStatus;
import com.girdharshukla.jobqueue.repositories.JobRepository;

import io.lettuce.core.KeyValue;
import io.lettuce.core.api.sync.RedisCommands;

@Component
public class Worker implements Runnable {
    private final RedisCommands<String, String> commands;
    private final JobRepository jobRepository;
    private final Map<String, JobHandler> handlers;

    public Worker(@Qualifier("workerCommands") RedisCommands<String, String> commands, JobRepository jobRepository,
            Map<String, JobHandler> handlers) {
        this.commands = commands;
        this.jobRepository = jobRepository;
        this.handlers = handlers;
    }

    @Override
    public void run() {
        while (true) {
            KeyValue<String, String> ans = commands.brpop(0, "jobs_queue");
            if (ans == null) {
                continue;
            }
            String jobId = ans.getValue();
            UUID id = UUID.fromString(jobId);
            Job job = jobRepository.findById(id);
            if (job == null) {
                continue;
            }
            JobHandler handler = handlers.get(job.getType());
            if (handler == null) {
                jobRepository.transition(id, JobStatus.FAILED, "Unknown job type");
                continue;
            }
            jobRepository.transition(id, JobStatus.RUNNING, null);
            try {
                handler.execute(job.getPayload());
                jobRepository.transition(id, JobStatus.SUCCESS, null);
            } catch (Exception e) {
                e.printStackTrace();
                if (job.getAttempt() + 1 < job.getMaxAttempts()) {
                    jobRepository.transition(id, JobStatus.QUEUED, e.getMessage());
                    commands.lpush("jobs_queue", id.toString());
                } else {
                    jobRepository.transition(id, JobStatus.FAILED, "Max attempts reached");
                }
            }

        }
    }
}
