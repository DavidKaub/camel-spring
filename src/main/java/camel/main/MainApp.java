package camel.main;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sudoku.solver.EmailBox;
import sudoku.solver.EmailHandler;

public class MainApp {

    private static String emailAdress = "";
    private static String imapServer = "imap.gmail.com";
    private static int imapPort = 993;
    private static String imapUsername = "sudokusolver2019";
    private static String smtpServer = "smtp.gmail.com";
    private static int smptPort = 465;
    private static String smtpUsername = "sudokusolver2019@gmail.com";
    private static String password = "#sudokuSolver2019";




    public static void main(String[] args) {
        int minutesToRunUntilAutoStop = 10;
        /**
         * TODO:
         * 1. Send Message to BoxManager
         * 2. Initialize Box
         */


        /**
         * the following has to be implemented in Camel!!
         *
         *
         * do initial stuff - retrieve boxName, boxValue ip for MQTT
         * TODO establish connection to boxManager retrieve and set initial values and boxName to SudokuBox sudokuBox
         * Initiale Nachricht an den Manager
         * Box erfragt alle relevanten Parameter:
         * http://<SpringBoxManager>/api/initalize
         * Der Manager sendet in diesem Fall folgende Nachricht zurück:
         * {
         * "mqtt-ip" : a.b.c.d,
         * "mqtt-port" : 42,
         * "box" : "sudoko/box_a4",
         * "init" : [
         * { "21" : 5 },
         * { "01" : 7 }
         * ]
         * }
         *
         */


        String boxName = "TODO";
        String initialValues = "TODO";
        EmailBox sudokuBox = new EmailBox(boxName,initialValues);
        EmailHandler emailHandler = new EmailHandler(sudokuBox,emailAdress, imapServer,imapPort,imapUsername,smtpServer,smptPort,smtpUsername,password);
        emailHandler.start();

        /**
         * 3. Start MQTT / Email Routes
         * 4. Send OK TO BoxManager -> best case by email via camel
         */




        //BasicConfigurator.configure();
        AbstractApplicationContext sendMailContext = new ClassPathXmlApplicationContext("applicationContext-camel.xml");
        AbstractApplicationContext receiveMailContext = new ClassPathXmlApplicationContext("applicationContext-receiveEmail.xml");
        sendMailContext.start();
        //receiveMailContext.start();
        System.out.println("Application context started");
        try {
            for(int i = 0; i < minutesToRunUntilAutoStop; i++){
                System.out.println(minutesToRunUntilAutoStop-i+" minutes to go until camel shuts down");
                Thread.sleep(60 * 1000);
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        //TODO close all routes
        sendMailContext.stop();
        receiveMailContext.stop();
        sendMailContext.close();
        receiveMailContext.close();

    }
}