package com.javainuse.route;

import com.javainuse.bean.MyTransformer;
import com.javainuse.processor.MyProcessor;
import org.apache.camel.builder.RouteBuilder;

public class ReceiveMailRoute  extends RouteBuilder{

        @Override
        public void configure() throws Exception {
            from("imap://localhost:143?username=email1&password=apfelmusmann")//"file:C:/inputFolder?move=./done"
                    .process(new MyProcessor())
                    .bean(new MyTransformer(), "TransformContext")
                    .process(new MyProcessor())
                    .to("file:C:/mailOutputFolder");
        }
}
