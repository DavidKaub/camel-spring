package camel.route;

import camel.processor.ProcessorEmailToMqtt;
import org.apache.camel.builder.RouteBuilder;

public class MailToMqttRoute extends RouteBuilder{

    private String mqttUrl;
    private String mqttPort;
    private String boxNameForMqtt;


    private String boxName;

    private String emailAdress;
    private String imapServer;
    private int imapPort;
    private boolean ssl;
    private String imapUsername;
    private String password;
    private int delay;
    private boolean debug;


    public MailToMqttRoute(String boxName, String boxNameForMqtt, String mqttUrl, String mqttPort, String emailAdress, String imapServer, int imapPort, boolean ssl, String imapUsername, String password, int delay, boolean debug) {
        this.boxNameForMqtt = boxNameForMqtt;
        this.mqttUrl = mqttUrl;
        this.mqttPort = mqttPort;
        this.emailAdress = emailAdress;
        this.imapServer = imapServer;
        this.imapPort = imapPort;
        this.ssl = ssl;
        this.imapUsername = imapUsername;
        this.password = password;
        this.delay = delay;
        this.debug = debug;
        this.boxName = boxName;
    }

    @Override
        public void configure() throws Exception {
        // from("imaps://imap.gmail.com:993?username=sudokusolver2019&password=#sudokuSolver2019&delay=5")//"file:C:/inputFolder?move=./done"
        //from("imap://localhost:143?username=email2&password=apfelmusmann")//"file:C:/inputFolder?move=./done"
        //from("imap://192.168.178.42:143?username=email1&password=apfelmusmann&delay=5")//"file:C:/inputFolder?move=./done"


        String imapString = "imap" + (this.ssl ? 's' : "") + "://"+imapServer+":"+imapPort+"?username="+imapUsername+"&password="+password+"&delay="+delay+ (this.debug ? "&debugMode=true" : "");
        //System.out.println(imapString);
            from(imapString)
                    //processor muss topic setzten
                    .process(new ProcessorEmailToMqtt(boxName,boxNameForMqtt))
                    .choice()
                    .when(header("type").isEqualTo("knowledge"))
                    .to("mqtt:bar?host=tcp://" + this.mqttUrl + ":" + this.mqttPort+"&publishTopicName=" + this.boxNameForMqtt)
                    .when(header("type").isEqualTo("result"))
                    .to("mqtt:bar?host=tcp://" + this.mqttUrl + ":" + this.mqttPort+"&publishTopicName=" + this.boxNameForMqtt + "/result");

    }
}
