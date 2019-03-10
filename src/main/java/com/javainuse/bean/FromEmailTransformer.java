package com.javainuse.bean;

public class FromEmailTransformer {


    public String TransformContext(String body) {
        //
        if (body.contains("token")) {
            return transformEmailToMqtt(body);
        } else if (body.contains("token2")) {
            //TODO
        }
        //TODO default!
        //String upper = body.toUpperCase();
        return body;

    }

    private String transformEmailToMqtt(String body) {
        //TODO
        return null;
    }


    private String transformEmailToRest(String body){
        return null;
    }
}
