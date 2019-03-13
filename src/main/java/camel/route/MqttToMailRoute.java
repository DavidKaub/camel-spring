package camel.route;

import org.apache.camel.builder.RouteBuilder;
import camel.processor.ProcessorMqttToEmail;
import sudoku.solver.EmailHandler;

public class MqttToMailRoute extends RouteBuilder {


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
    private String boxName;
    private EmailHandler emailHandler;

    private String imapServer;
    private String imapUsername;
    private int imapPort;
    private int imapPollingDelay;


    public String getImapServer() {
        return imapServer;
    }

    public String getImapUsername() {
        return imapUsername;
    }

    public int getImapPort() {
        return imapPort;
    }

    public int getImapPollingDelay() {
        return imapPollingDelay;
    }

    public MqttToMailRoute(
            String boxName, String boxNameForMqtt, String mqttUrl, String mqttPort, String emailAdress, String smtpServer, int smtpPort, boolean ssl, String smtpUsername, String password, boolean debug, String receiverEmailAdress, EmailHandler emailHandler, String imapUsername, String imapServer,int imapPort, int imapPollingDelay) {
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
        this.boxName = boxName;
        this.emailHandler = emailHandler;
        this.imapServer = imapServer;
        this.imapPort = imapPort;
        this.imapUsername = imapUsername;
        this.imapPollingDelay = imapPollingDelay;
    }

    @Override
    public void configure() throws Exception {
        System.out.println("configure !");

        String smtpString = "smtp" + (this.ssl ? 's' : "") + "://"+smtpServer+":"+smtpPort+"?username=" + smtpUsername+"&password=" + password +"&to="+receiverEmailAdress+ (this.debug ? "&debugMode=true" : "");
        //System.out.println(smtpString);

        String mqtt = "mqtt:bar?host=tcp://" + this.mqttUrl + ":" + this.mqttPort + "&subscribeTopicNames=sudoku/+";
        System.out.println(mqtt);

        from(mqtt)
                .process(new ProcessorMqttToEmail(boxNameForMqtt,boxName,mqttUrl,mqttPort, emailHandler,this))
                .to(smtpString);
    }


    public String getMqttUrl() {
        return mqttUrl;
    }

    public String getMqttPort() {
        return mqttPort;
    }

    public String getBoxNameForMqtt() {
        return boxNameForMqtt;
    }

    public String getEmailAdress() {
        return emailAdress;
    }

    public String getSmtpServer() {
        return smtpServer;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public boolean isSsl() {
        return ssl;
    }

    public String getSmtpUsername() {
        return smtpUsername;
    }

    public String getPassword() {
        return password;
    }

    public boolean isDebug() {
        return debug;
    }

    public String getReceiverEmailAdress() {
        return receiverEmailAdress;
    }

    public String getBoxName() {
        return boxName;
    }

    public EmailHandler getEmailHandler() {
        return emailHandler;
    }
}
