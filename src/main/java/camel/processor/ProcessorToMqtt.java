package camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class ProcessorToMqtt implements Processor {

    private String boxName;
    private String boxNameForMqtt;




    public void process(Exchange exchange) throws Exception {
        System.out.println("Now processing Input " + exchange.getIn().getBody(String.class));
        /**
         * 1. lese email
         * 2. prüfe ob es wissen ist oder ob es result ist (ggf über betreff falls so definiert)
         * 3. passe es jeweils so an, dass es eine json ist
         */
    }

}
