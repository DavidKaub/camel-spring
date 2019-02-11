package com.javainuse.bean;

public class MyTransformer {

    public String TransformContext(String body){
        String upper = body.toUpperCase();
        return upper;

    }

}
