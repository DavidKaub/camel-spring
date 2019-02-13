package com.javainuse.route;

import com.javainuse.bean.MyTransformer;
import com.javainuse.processor.MyProcessor;
import org.apache.camel.builder.RouteBuilder;

public class ReceiveMailRoute  extends RouteBuilder{

        @Override
        public void configure() throws Exception {
            //from("imaps://imap.gmail.com:993?username=sudokusolver2019&password=#sudokuSolver2019")//"file:C:/inputFolder?move=./done"
            //from("imap://localhost:143?username=email2&password=apfelmusmann")//"file:C:/inputFolder?move=./done"
            from("imap://192.168.178.42:143?username=email1&password=apfelmusmann&delay=5")//"file:C:/inputFolder?move=./done"
                    //&debugMode=true
                    .process(new MyProcessor())
                    .bean(new MyTransformer(), "TransformContext")
                    .process(new MyProcessor())
                    .to("file:C:/mailOutputFolder");
        }
}
