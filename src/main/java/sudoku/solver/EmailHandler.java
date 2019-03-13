package sudoku.solver;

import java.io.IOException;
import java.util.List;

public class EmailHandler extends NetworkHandler {
    private String emailAdress;
    private String imapServer;
    private int imapPort;
    private String imapUsername;
    private String smtpServer;
    private int smptPort;
    private String smtpUsername;
    private String password;
    private boolean sslEnabled;
    private String emailAdressReceiver;
    private boolean initialized = false;

    public EmailHandler(EmailBox sudokuBox, String emailAdress, String imapServer, int imapPort, String imapUsername, String smtpServer, int smptPort, String smtpUserName, String password, boolean sslEnabled, String emailAdressReceiver){
        super(sudokuBox);
        this.emailAdress = emailAdress;
        this.imapServer = imapServer;
        this.imapPort = imapPort;
        this.imapUsername = imapUsername;
        this.smtpServer = smtpServer;
        this.smptPort = smptPort;
        this.smtpUsername = smtpUserName;
        this.password = password;
        this.sslEnabled = sslEnabled;
        sudokuBox.setNetworkHandler(this);
        establishConnectionToManager();
        this.emailAdressReceiver = emailAdressReceiver;
        System.out.println("initialized email handler");
    }

    @Override
    public void run() {
        if(!initialized){
            sudokuBox.init();
            System.out.println("started email handler service");
            this.initialized = true;
        }
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
            readEmailsFromServer();
            //gibt der box neues wissen & damit reagiert die box, solving darauf
            //TODO: only commented for test purpuses
            messageProcessing();

            /**
             * ORIGINAL VERSION!
             *
             * readEmailsFromServer();
             * messageProcessing();
             *
             * thats it!
             */


        }
        /**
         * 1. Read messages from all incoming connections
         * 3. feed the box with the new knowledge
         *      b. this means also processing the boxes solving algorithm and retrieving new knowledge
         */
    }


    void readEmailsFromServer() {
        if (lockForIncomingMessages.tryLock()) {
            // Got the lock
            try {

                /**
                 *TODO read emails from server. Write body String to list incomingMessages
                 * also hier sollte der JavaMail Code hin
                 *
                 * 1. establish connection to email server
                 * 2. Read emails.....
                 */
                sudoku.solver.mailer.Mailer mailer = new sudoku.solver.mailer.Mailer();
                List<String> contents = mailer.receive(emailAdress, password,(this.sslEnabled ? "imaps" : "imap"),imapServer,imapPort+"");
                if(contents != null)
                {
                    for (String content :
                            contents) {
                        System.out.println("The Content: " + content);
                        incomingMessages.add(content);
                    }
                }
                        //mailer.testMailer();
                //Das Hinzufügen kann volgenderMaßen funktionieren
                /*boolean dummy = false;
                while(dummy){//solange neue Nachrichten vom server verfügbar sind
                    String body = "INahlt des Email Bodys!";
                    incomingMessages.add(body);
                    System.out.println("Endlosschleife");
                }*/
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

    @Override
    void establishConnectionToManager() {
       //macht nix!
    }

    @Override
    void sendIsSolved() {




        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("RESULT," + "BOX_"+sudokuBox.getBoxName() + ",");
        stringBuilder.append(sudokuBox.printResult().trim());
        sudoku.solver.mailer.Mailer mailer = new sudoku.solver.mailer.Mailer();
        mailer.sendMail(stringBuilder.toString(),"BOX_"+boxName+" RESULT",emailAdress,smtpUsername,emailAdressReceiver,password,sslEnabled,smtpServer,smptPort+"");

        /**
         * Ergebnisnachricht an den Manager
         * http://<SpringBoxManager>/api/result
         * Inhalt der Nachricht:
         * {
         * "box" : "sudoku/box_a1",
         * "result" : [ 2, 3, 1, 4, 7, 6, 9, 8, 5 ]
         * }
         *
         *
         *REST API
         * https://stackoverflow.com/questions/6464120/rest-api-for-java
         * JAX-RS is the standard Java API for RESTful web services. Jersey is the reference implementation for this, it has server-side as well as client-side APIs (so, ways to expose methods in your code as RESTful web services, as well as ways to talk to RESTful web services running elsewhere).
         *
         * There are also other implementations of JAX-RS, for example Apache CXF and JBoss RESTEasy.
         */
        //the solution against endless loop?
        sentSolvedMessage = true;
    }



    @Override
    void sendPendingMessages() {
        sudoku.solver.mailer.Mailer mailer = new sudoku.solver.mailer.Mailer();
        boolean done = false;
        int tryNo = 0;
        //1. TODO establish connection to email server:
        while (!done && tryNo < 5) {
            tryNo++;
            //get lock
            if (lockForOutgoingMessages.tryLock()) {
                // Got the lock
                try {
                    for (String message : outgoingMessages) {
                        //2. send each Message as different email!(String message as email body!)
                        // Subject is practically irrelevant? No receiver needed due to mqtt
                        // -> receiver could be added to subject (to simulate email application)
                        //TODO the camel instance needs to translate this message to a json string!
                        mailer.sendMail(message,"Knowledge from BOX_"+boxName,emailAdress,smtpUsername,emailAdressReceiver,password,sslEnabled,smtpServer,smptPort+"");

                    }
                    outgoingMessages.clear();

                } finally {
                    // Make sure to unlock so that we don't cause a deadlock
                    lockForOutgoingMessages.unlock();
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
    }
}
