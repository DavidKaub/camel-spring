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
                .to("smtps://smtp.gmail.com:465?username=sudokusolver2019@gmail.com&password=#sudokuSolver2019&to=sudokusolver2019@gmail.com");
                //.to("smtp://localhost:25?username=email2&password=apfelmusmann&to=email2@localhost");
                //.to("smtp://192.168.178.42:587?username=email1@localhost&password=apfelmusmann&to=email1@localhost");
        //&debugMode=true
    }
}
