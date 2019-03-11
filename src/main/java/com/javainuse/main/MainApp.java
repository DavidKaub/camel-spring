package com.javainuse.main;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainApp {


    public static void main(String[] args) {
        //BasicConfigurator.configure();
        AbstractApplicationContext sendMailContext = new ClassPathXmlApplicationContext("applicationContext-camel.xml");
        AbstractApplicationContext receiveMailContext = new ClassPathXmlApplicationContext("applicationContext-receiveEmail.xml");
        sendMailContext.start();
        //receiveMailContext.start();
        System.out.println("Application context started");
        try {
            Thread.sleep(5 * 60 * 1000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendMailContext.stop();
        receiveMailContext.stop();
        sendMailContext.close();
        receiveMailContext.close();

    }
}