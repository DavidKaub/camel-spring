package camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.IllegalFormatException;

public class ProcessorEmailToMqtt implements Processor {

    private String boxName;
    private String boxNameForMqtt;


    public ProcessorEmailToMqtt(String boxName, String boxNameForMqtt){
        this.boxName = boxName;
        this.boxNameForMqtt = boxNameForMqtt;
    }




    public void process(Exchange exchange) throws Exception {

    String emailBody = exchange.getIn().getBody(String.class);
        System.out.println("Now processing Input " + emailBody);
        /**
         * 1. lese email
         * 2. prüfe ob es wissen ist oder ob es result ist (ggf über betreff falls so definiert)
         * 3. passe es jeweils so an, dass es eine json ist
         */




        //wenn es wissen ist pürfe auf absolutes oder relative wissen:
        if (!emailBody.contains("BOX_")) {
            emailBody = convertAbsoluteToRelativeKnowledge(emailBody);
            //dann ist es absolutes Wissen und muss umgewandlet werden




        }
    }

    private String convertAbsoluteToRelativeKnowledge(String knwoledge) throws Exception {
        StringBuilder stringBuilderForRelativeKnowsledge = new StringBuilder();
        char colOfKnowledge = knwoledge.charAt(0);
        char rowOfKnowledge = knwoledge.charAt(1);
        char value = knwoledge.charAt(3);
        //z.B. D5:7 in BOX_D4,0,1:7

        char colOfBox = boxName.charAt(4);
        char rowOfBox = boxName.charAt(5);

        int colOffset = colOfKnowledge - colOfBox;
        int rowOffset = rowOfKnowledge - rowOfBox;

        if(colOfBox > 2 || rowOffset > 2 ||colOffset < 0 || rowOffset < 0){
            throw new Exception();
        }

        stringBuilderForRelativeKnowsledge.append(boxName);
        stringBuilderForRelativeKnowsledge.append(",");
        stringBuilderForRelativeKnowsledge.append(colOffset);
        stringBuilderForRelativeKnowsledge.append(",");
        stringBuilderForRelativeKnowsledge.append(rowOffset);
        stringBuilderForRelativeKnowsledge.append(":");
        stringBuilderForRelativeKnowsledge.append(value);

        return stringBuilderForRelativeKnowsledge.toString();
    }

}
