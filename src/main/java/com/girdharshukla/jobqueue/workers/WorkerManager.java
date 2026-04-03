package com.girdharshukla.jobqueue.workers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

@Component
public class WorkerManager {
    public WorkerManager(Worker worker){
        ExecutorService exe = Executors.newFixedThreadPool(8);
        for(int i = 0; i < 8; i++){
            exe.submit(worker);
        }
    }
}
