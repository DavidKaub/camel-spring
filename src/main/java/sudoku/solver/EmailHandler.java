package sudoku.solver;

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

    public EmailHandler(SudokuBox sudokuBox, String emailAdress, String imapServer, int imapPort, String imapUsername, String smtpServer, int smptPort, String smtpUserName, String password){
        super(sudokuBox);
        this.emailAdress = emailAdress;
        this.imapServer = imapServer;
        this.imapPort = imapPort;
        this.imapUsername = imapUsername;
        this.smtpServer = smtpServer;
        this.smptPort = smptPort;
        this.smtpUsername = smtpUserName;
        this.password = password;
        establishConnectionToManager();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
            readEmailsFromServer();
            //gibt der box neues wissen & damit reagiert die box, solving darauf
            establishConnectionToManager();
            sendUpdate();
            sendIsSolved();

            readEmailsFromServer();
            //TODO: only commented for test purpuses
            //messageProcessing();

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
                List<String> contents = mailer.receive("sudokusolver2019@gmail.com", "#sudokuSolver2019");
                if(contents != null)
                {
                    for (String content :
                            contents) {
                        System.out.println("The Content: " + content);
                    }
                }

                        //mailer.testMailer();

                //Das Hinzufügen kann volgenderMaßen funktionieren
                boolean dummy = false;
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

    @Override
    void establishConnectionToManager() {
        /**
         * //TODO optinal!
         *Wenn die Box fertig initialisiert ist, teilt sie dies dem BoxManager über mit
         * -> camel ruft dann "http://<SpringBoxManager>/api/ready?box=<sudoku/box_a1>" mit dem korrekten BoxNamen auf
         */
        sudoku.solver.mailer.Mailer mailer = new sudoku.solver.mailer.Mailer();
        mailer.sendGmail("http:/<SpringBoxManager>/api/ready?box=<sudoku/box_" + sudokuBox.boxName + ">", "From Box To SpringBoxManager");
    }


    @Override
    void sendIsSolved() {
        //TODO
        //send the is Solved message to boxManager (here via email!)
        //an der stelle könnte man auch einfach nur bei den ausgehenden nachrichten outgoingMessages einen zusätzlichne string hinzufügen,

        //list all found values comma separated
        StringBuffer sb = new StringBuffer();
        for(int col=0; col<=2; col++)
        {
            for(int row=0; row<=2; row++)
            {
                SudokuCell cell = sudokuBox.boxCells[col][row];
                sb.append(cell.getValue());
                if((col == 2) && (row == 2))
                {
                    //don't append anything
                }else
                {
                    sb.append(",");
                }
            }
        }
        //send result-Message via Mail
        sudoku.solver.mailer.Mailer mailer = new sudoku.solver.mailer.Mailer();
        mailer.sendGmail("{\"box\":\"sudoku/box_" + sudokuBox.boxName + "\",\"result\":["+ sb.toString() + "]}");

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

    void sendUpdate() {
        //TODO
        //send the Update message to MQTT (here via email!)
        //an der stelle könnte man auch einfach nur bei den ausgehenden nachrichten outgoingMessages einen zusätzlichne string hinzufügen,


        /* the message's format
        {
        "box" : "sudoku/box_a1";
        "r_column" : 0,
        "r_row" : 1,
        "value" : 4
        }
         */

        //send result-Message via Mail
        sudoku.solver.mailer.Mailer mailer = new sudoku.solver.mailer.Mailer();
        int columnNumber = 0;
        int rowNumber = 1;
        mailer.sendGmail("{\"box\":\"sudoku/box_" + sudokuBox.boxName + "\","
                + "\"r_column\":" + columnNumber + ","
                + "\"r_row\":" + rowNumber + ","
                + "\"value\":" + "CELLVALUE" + "}", "From Box To MQTT");

        //the solution against endless loop?
        sentSolvedMessage = true;
    }

    @Override
    void sendPendingMessages() {
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
                        //2. TODO send each Message as different email!(String message as email body!)
                        // Subject is practically irrelevant? No receiver needed due to mqtt
                        // -> receiver could be added to subject (to simulate email application)
                        //TODO the camel instance needs to translate this message to a json string!

                        for (String neighborName : sudokuBox.getNeighborNames()) {
                            //optional Send email for each neighbor! (add neighbor name to email subject)
                        }
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
