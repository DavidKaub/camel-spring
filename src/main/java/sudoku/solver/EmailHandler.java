package sudoku.solver;

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
            messageProcessing();
        }
        /**
         * 1. Read messages from al l incoming connections
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

                //Das Hinzufügen kann volgenderMaßen funktionieren
                boolean dummy = false;
                while(dummy){//solange neue Nachrichten vom server verfügbar sind
                    String body = "INahlt des Email Bodys!";
                    incomingMessages.add(body);
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
    }


    @Override
    void sendIsSolved() {
        //TODO
        //send the is Solved message to boxManager (here via email!)

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
                        //2. TODO send each Message as different email!(String message as email body!) Subject is practically irrelevant? No receiver needed due to mqtt -> receiver could be added to subject (to simulate email application)
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
