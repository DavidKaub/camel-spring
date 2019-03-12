package camel.route;

import camel.bean.MQTTConverter;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import java.util.HashMap;

public class SudokuRouteBuilder extends RouteBuilder{


    private String mqttHost;
    private String boxID;
    private static HashMap<String, String[]> neighbors = new HashMap<String, String[]>() {
        {
            put("box_a1", new String[]{"box_a4", "box_d1"});
            put("box_a4", new String[]{"box_a1", "box_a7", "box_d4"});
            put("box_a7", new String[]{"box_a4", "box_d7"});
            put("box_d1", new String[]{"box_a1", "box_d4", "box_g1"});
            put("box_d4", new String[]{"box_a4", "box_d1", "box_d7", "box_g4"});
            put("box_d7", new String[]{"box_d4", "box_a7", "box_g7"});
            put("box_g1", new String[]{"box_d1", "box_g4"});
            put("box_g4", new String[]{"box_g1", "box_d4", "box_g7"});
            put("box_g7", new String[]{"box_g4", "box_d7"});
        }
    };

    SudokuRouteBuilder(String mqttHost, String boxID, boolean sslEnabled) {
        this.mqttHost = mqttHost;
        this.boxID = boxID;
    }


    public void configure() {
        // ICH WEIß, DASS DIE KEYS NICHT INS ÖFFENTLICHE GIT SOLLTEN! IST MIR ABER EGAL. NACH DEM PROJEKT WERDEN DIE BOTS GELÖSCHT
        System.out.println("Setting up camel-routes");

        // Build Subscribe URI
        StringBuilder subscribeTopics = new StringBuilder();
        String sep = "";
        for (String neighbor: neighbors.get(this.boxID)) {
            subscribeTopics.append(sep).append("sudoku/").append(neighbor);
            sep = ",";
        }

        System.out.println("Subscribing to topics: " + subscribeTopics.toString());
        from("mqtt:sudoku?host=" + this.mqttHost + "&subscribeTopicNames=" + subscribeTopics.toString())
                .transform(body().convertToString())
                .bean(MQTTConverter.class)
                .log("Received fieldConfiguration via MQTT-Topic. Forwarding it to Email.")
                .to("smtps://smtp.gmail.com:465?username=sudokusolver2019@gmail.com&password=#sudokuSolver2019&to=sudokusolver2019@gmail.com");


        // Publish new knowledge with own id
        from("imaps://imap.gmail.com:993?username=sudokusolver2019&password=#sudokuSolver2019&delay=5")
                .choice()
                .when(body().startsWith("/ready"))
                //.bean(TelegramBot.class)
                .to("mqtt:sudoku?host=" + this.mqttHost + "&publishTopicName=sudoku/" + boxID + "/result")
                .otherwise()
               // .bean(TelegramBot.class)
                .to("mqtt:sudoku?host=" + this.mqttHost + "&publishTopicName=sudoku/" + boxID);

        // Start Message
        from("mqtt:sudoku?subscribeTopicNames=sudoku/start")
                .log("Received start signal via MQTT from")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setBody("/start");
                    }
                })
                .to("smtps://smtp.gmail.com:465?username=sudokusolver2019@gmail.com&password=#sudokuSolver2019&to=sudokusolver2019@gmail.com");
    }


}
