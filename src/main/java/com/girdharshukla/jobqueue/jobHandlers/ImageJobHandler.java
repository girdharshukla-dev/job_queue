package com.girdharshukla.jobqueue.jobHandlers;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

//these are just simulations, any job can be implemented throug the JobHandler interface
@Component("image.process")
public class ImageJobHandler implements JobHandler {

    //assume payload has {imageId: <int>}
    @Override
    public void execute(JsonNode payload) throws Exception {
        int imageId = payload.get("imageId").asInt();
        System.out.println("Inside image jobHandler with imageId as: " + imageId);
        Thread.sleep(1000);
        System.out.println("Image processed: " + imageId);
    }
}
