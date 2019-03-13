package sudoku.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Initializer {
    public void sendInitialize()
    {
        sudoku.solver.mailer.Mailer mailer = new sudoku.solver.mailer.Mailer();
        //mailer.sendGmail("http:/<SpringBoxManager>/api/initialize", "From Box To SpringBoxManager");
    }

/*


    public String readInitialValuesFromEmailServer(String emailAdress, String password) {
        List<String> incomingMessages = new ArrayList<String>();
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
                }
            } finally {
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
        }
    }
*/

    public void sendStartMessage() {
        sudoku.solver.mailer.Mailer mailer = new sudoku.solver.mailer.Mailer();
        //mailer.sendGmail("sudoku/start", "From SpringBoxManager To MQTT (Faked by Box for test purposes");
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
        //List<String> contents = mailer.receive(emailAdress, password);
        /*if(contents != null)
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
        }*/
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
