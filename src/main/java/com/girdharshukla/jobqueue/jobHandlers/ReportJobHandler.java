package com.girdharshukla.jobqueue.jobHandlers;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

//these are just simulations, any job can be implemented throug the JobHandler interface
@Component("report.generate")
public class ReportJobHandler implements JobHandler{

    //assuming the payload is {userId: <int>}
    @Override
    public void execute(JsonNode payload) throws Exception {
        int userId = payload.get("userId").asInt();
        Thread.sleep(3000);
        System.out.println("Generated report for user: " + userId);
    }
    
}
