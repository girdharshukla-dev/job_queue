package com.girdharshukla.jobqueue.controllers;

import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/jobs")
public class JobController {

    public record SubmitJobRequest(String type, JsonNode payload) {}
    public record SubmitJobResponse(UUID id) {}
    @PostMapping
    public SubmitJobResponse submit(@RequestBody SubmitJobRequest submitJobRequest){
        
        return new SubmitJobResponse(UUID.randomUUID());
    }
}
