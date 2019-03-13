package sudoku.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Initializer {
    public void sendInitialize()
    {
        sudoku.solver.mailer.Mailer mailer = new sudoku.solver.mailer.Mailer();
        mailer.sendGmail("http:/<SpringBoxManager>/api/initialize", "From Box To SpringBoxManager");
    }


    public void sendInitialValues() {
        /*
        {
            "mqtt-ip":a.b.c.d,
            "mqtt-port":42,
            "box":"sudoku/box_a4",
            "init":[
            {"21":5 },
            {"01":7}
            ]
        }
         */
        sudoku.solver.mailer.Mailer mailer = new sudoku.solver.mailer.Mailer();
        Map<String,Integer> initValues = new HashMap<String,Integer>();

        String boxName = "a4";
        //define initial Values
        initValues.put("21", 5);
        initValues.put("01", 7);

        //format initial Values
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for (Map.Entry entry :
                initValues.entrySet()) {
            sb.append("{\"" + entry.getKey() + "\":" + entry.getValue() + "}");
            if(counter+1 == initValues.size())
            {
                sb.append("\n");
            }else
            {
                sb.append(",\n");
            }
            counter++;
        }
        mailer.sendGmail("        {\n" +
                "            \"mqtt-ip\":a.b.c.d,\n" +
                "            \"mqtt-port\":42,\n" +
                "            \"box\":\"sudoku/box_"+ boxName + "\",\n" +
                "            \"init\":[\n" +
                sb.toString() +
                "            ]\n" +
                "        }", "From SpringBoxManager To Gmail (faked from Mailer for test purposes)");
    }

    public String readInitialValuesFromEmailServer(String emailAdress, String password) {
        List<String> incomingMessages = new ArrayList<String>();
        /*if (lockForIncomingMessages.tryLock()) {
            // Got the lock
            try {
            */
                /**
                 *TODO read emails from server. Write body String to list incomingMessages
                 * also hier sollte der JavaMail Code hin
                 *
                 * 1. establish connection to email server
                 * 2. Read emails.....
                 */
                sudoku.solver.mailer.Mailer mailer = new sudoku.solver.mailer.Mailer();
                List<String> contents = mailer.receive(emailAdress, password);
                if(contents != null)
                {
                    for (String content :
                            contents) {
                        System.out.println("The Content: " + content);
                        incomingMessages.add(content);
                    }
                }
        for (String content :
                incomingMessages) {
            if (content.contains("\"init\""))
            {
                return content;
            }
        }
        return null;
                //mailer.testMailer();
                //Das Hinzufügen kann volgenderMaßen funktionieren
                /*boolean dummy = false;
                while(dummy){//solange neue Nachrichten vom server verfügbar sind
                    String body = "INahlt des Email Bodys!";
                    incomingMessages.add(body);
                    System.out.println("Endlosschleife");
                }*/
            /*} finally {
                // Make sure to unlock so that we don't cause a deadlock
                lockForIncomingMessages.unlock();
            }
        } else {
            noLockNotification();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    public void sendStartMessage() {
        sudoku.solver.mailer.Mailer mailer = new sudoku.solver.mailer.Mailer();
        mailer.sendGmail("sudoku/start", "From SpringBoxManager To MQTT (Faked by Box for test purposes");
    }

    public String readStartMessageFromEmailServer(String emailAdress, String password) {
        List<String> incomingMessages = new ArrayList<String>();
        /*if (lockForIncomingMessages.tryLock()) {
            // Got the lock
            try {
            */
        /**
         *TODO read emails from server. Write body String to list incomingMessages
         * also hier sollte der JavaMail Code hin
         *
         * 1. establish connection to email server
         * 2. Read emails.....
         */
        sudoku.solver.mailer.Mailer mailer = new sudoku.solver.mailer.Mailer();
        List<String> contents = mailer.receive(emailAdress, password);
        if(contents != null)
        {
            for (String content :
                    contents) {
                System.out.println("The Content: " + content);
                incomingMessages.add(content);
            }
        }
        for (String content :
                incomingMessages) {
            if (content.contains("\"sudoku/start\""))
            {
                return content;
            }
        }
        return null;
        //mailer.testMailer();
        //Das Hinzufügen kann volgenderMaßen funktionieren
                /*boolean dummy = false;
                while(dummy){//solange neue Nachrichten vom server verfügbar sind
                    String body = "INahlt des Email Bodys!";
                    incomingMessages.add(body);
                    System.out.println("Endlosschleife");
                }*/
            /*} finally {
                // Make sure to unlock so that we don't cause a deadlock
                lockForIncomingMessages.unlock();
            }
        } else {
            noLockNotification();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }
}
