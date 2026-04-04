package com.girdharshukla.jobqueue.models;

import java.sql.Timestamp;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

public class Job {

    public enum JobStatus { QUEUED, RUNNING, SUCCESS, FAILED};

    private UUID id;
    private String type;
    private JsonNode payload;
    private JobStatus status;
    private int attempt;
    private int maxAttempts;
    private Timestamp createdAt;
    private Timestamp startedAt;
    private Timestamp finishedAt;
    private JsonNode result;
    private String error;

    public void setId(UUID id){
        this.id = id;
    }
    public UUID getId(){
        return this.id;
    }

    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }

    public void setPayload(JsonNode payload){
        this.payload = payload;
    }
    public JsonNode getPayload(){
        return this.payload;
    }

    public void setStatus(JobStatus status){
        this.status = status;
    }
    public JobStatus getStatus(){
        return this.status;
    }

    public void setAttempt(int attempt){
        this.attempt = attempt;
    }
    public int getAttempt(){
        return this.attempt;
    }

    public void setMaxAttempts(int maxAttempts){
        this.maxAttempts = maxAttempts;
    }
    public int getMaxAttempts(){
        return this.maxAttempts;
    }

    public void setCreatedAt(Timestamp createdAt){
        this.createdAt = createdAt;
    }
    public Timestamp getCreatedAt(){
        return this.createdAt;
    }

    public void setFinishedAt(Timestamp finishedAt){
        this.finishedAt = finishedAt;
    }
    public Timestamp getFinishedAt(){
        return this.finishedAt;
    }

    public void setStartedAt(Timestamp startedAt){
        this.startedAt = startedAt;
    }
    public Timestamp getStartedAt(){
        return this.startedAt;
    }

    public void setResult(JsonNode result){
        this.result = result;
    }
    public JsonNode getResult(){
        return this.result;
    }

    public void setError(String error){
        this.error = error;
    }
    public String getError(){
        return this.error;
    }
}
