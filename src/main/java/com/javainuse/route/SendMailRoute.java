package com.javainuse.route;

import com.javainuse.bean.MyTransformer;
import org.apache.camel.builder.RouteBuilder;
import com.javainuse.processor.MyProcessor;

public class SendMailRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file:C:/mailInputFolder?move=./done")
                .process(new MyProcessor())
                .bean(new MyTransformer(), "TransformContext")
                .process(new MyProcessor())
                .to("smtp://localhost:587?username=email2&password=apfelmusmann&to=email2@localhost");
    }
}
