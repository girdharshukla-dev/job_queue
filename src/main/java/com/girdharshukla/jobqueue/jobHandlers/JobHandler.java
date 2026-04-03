package com.girdharshukla.jobqueue.jobHandlers;

import com.fasterxml.jackson.databind.JsonNode;

public interface JobHandler {
    public void execute(JsonNode payload) throws Exception;
}
