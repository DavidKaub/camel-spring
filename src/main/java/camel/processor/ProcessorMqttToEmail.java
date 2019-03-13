package camel.processor;

import camel.route.MailToMqttRoute;
import camel.route.MqttToMailRoute;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.main.Main;
import org.json.JSONObject;
import sudoku.solver.EmailHandler;

public class ProcessorMqttToEmail implements Processor {

    private String boxNameForMqtt;
    private String boxName;
    private String mqttUrl;
    private String mqttPort;
    private EmailHandler emailHandler;
    private MqttToMailRoute mqttToMailRoute;

    public ProcessorMqttToEmail(String boxNameForMqtt, String boxname, String mqttUrl, String mqttPort, EmailHandler emailHandler, MqttToMailRoute mqttToMailRoute) {
        this.boxNameForMqtt = boxNameForMqtt;
        this.boxName = boxname;
        this.mqttUrl = mqttUrl;
        this.mqttPort = mqttPort;
        this.emailHandler = emailHandler;
        this.mqttToMailRoute = mqttToMailRoute;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange.getIn().getHeaders());
        System.out.println("Now processing Output " + exchange.getIn().getBody(String.class));
        /**
         * 1. prüfe ob es wissen oder ob es start ist
         * 2. wenn es start ist, dann starte erst die route to mqtt UND emailHandler.start
         * sonst leite wissen über email weiter (setzt topic entsprechend, dass es nicht später gefiltert wird)
         */



    }


    private void initRoutesAndServices(){

        //1. route
        //TODO send Mail wird in receive Mail gestarted -> erst wenn start nachricht vorliegt
        Main mailToMqttMain = new Main();
        mailToMqttMain.addRouteBuilder(new MailToMqttRoute(boxName, boxNameForMqtt,mqttUrl,mqttPort,mqttToMailRoute.getEmailAdress(),mqttToMailRoute.getImapServer(),mqttToMailRoute.getImapPort(),mqttToMailRoute.isSsl(),mqttToMailRoute.getImapUsername(),mqttToMailRoute.getPassword(),mqttToMailRoute.getImapPollingDelay(),mqttToMailRoute.isDebug()));
        System.out.println("starting mailToMqttMain");
        try {
            mailToMqttMain.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("started mailToMqttMain");
        //2. handler
        emailHandler.start();

    }



    private void evaluateExchangeType(Exchange exchange) {
        if (exchange.getIn().getHeader("CamelMQTTSubscribeTopic", String.class).matches("^sudoku/box_[adg][147]$")) {
            exchange.getOut().setHeader("type", "knowledge");
            return;
        }

        if (exchange.getIn().getHeader("CamelMQTTSubscribeTopic", String.class).matches("^sudoku/start$")) {
            exchange.getOut().setHeader("type", "start");
            return;
        }

        exchange.getOut().setHeader("type", "unknown");
        return;
    }


    private void parseKnowledgeJSON(Exchange exchange) {
        String subscribedBox = (String) exchange.getIn().getHeader("CamelMQTTSubscribeTopic");

        System.out.println("Subscribed Box: " + subscribedBox);
        String json = exchange.getIn().getBody(String.class);

        System.out.println("Message from subscribed Box: " + json);

        StringBuilder b = new StringBuilder();
        JSONObject obj = new JSONObject(json);
        String boxnameRaw = obj.getString("box").trim();
        String boxname = "BOX_"
                + boxnameRaw.substring(boxnameRaw.length() - 2, boxnameRaw.length()).toUpperCase();

        b.append(boxname);
        b.append(",");
        b.append(obj.getInt("r_column"));
        b.append(",");
        b.append(obj.getInt("r_row"));
        b.append(":");
        b.append(obj.getInt("value"));

        String newKnowledge = b.toString();

        System.out.println(newKnowledge);

        exchange.getOut().setHeader(Exchange.FILE_NAME, boxname + "_" + System.currentTimeMillis());
        exchange.getOut().setBody(newKnowledge.toString(), String.class);
    }

}
