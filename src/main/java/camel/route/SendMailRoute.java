package camel.route;

import org.apache.camel.builder.RouteBuilder;
import camel.processor.ProcessorToEmail;

public class SendMailRoute extends RouteBuilder {


    private String mqttUrl;
    private String mqttPort;
    private String boxNameForMqtt;

    private String emailAdress;
    private String smtpServer;
    private int smtpPort;
    private boolean ssl;
    private String smtpUsername;
    private String password;
    private boolean debug;
    private String receiverEmailAdress;

    public SendMailRoute(
            String boxNameForMqtt, String mqttUrl, String mqttPort, String emailAdress, String smtpServer, int smtpPort, boolean ssl, String smtpUsername, String password, boolean debug, String receiverEmailAdress) {
        System.out.println("send mail route!");
        this.boxNameForMqtt = boxNameForMqtt;
        this.mqttUrl = mqttUrl;
        this.mqttPort = mqttPort;
        this.emailAdress = emailAdress;
        this.smtpServer = smtpServer;
        this.smtpPort = smtpPort;
        this.ssl = ssl;
        this.smtpUsername = smtpUsername;
        this.password = password;
        this.debug = debug;
        this.receiverEmailAdress = receiverEmailAdress;
    }

    @Override
    public void configure() throws Exception {
        System.out.println("configure !");

        String smtpString = "smtp" + (this.ssl ? 's' : "") + "://"+smtpServer+":"+smtpPort+"?username=" + smtpUsername+"&password=" + password +"&to="+receiverEmailAdress+ (this.debug ? "&debugMode=true" : "");
        System.out.println(smtpString);

        from("mqtt:bar?host=tcp://" + this.mqttUrl + ":" + this.mqttPort + "&subscribeTopicNames=sudoku/+")
                .process(new ProcessorToEmail())
                .to(smtpString);
    }
}
