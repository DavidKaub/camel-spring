package camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class ProcessorToEmail implements Processor {

    private String boxNameForMqtt;
    private String boxname;
    private String mqttUrl;
    private String mqttPort;

    public ProcessorToEmail(String boxNameForMqtt, String boxname, String mqttUrl, String mqttPort) {
        this.boxNameForMqtt = boxNameForMqtt;
        this.boxname = boxname;
        this.mqttUrl = mqttUrl;
        this.mqttPort = mqttPort;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange.getIn().getHeaders());
        System.out.println("Now processing Output " + exchange.getIn().getBody(String.class));
        /**
         * 1. prüfe ob es wissen oder ob es start ist
         * 2. wenn es start ist, dann starte erst die route to mqtt
         * sonst leite wissen über email weiter (setzt topic entsprechend, dass es nicht später gefiltert wird)
         */



    }

}
