package camel.main;
import camel.route.ReceiveMailRoute;
import camel.route.SendMailRoute;
import org.apache.camel.main.Main;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sudoku.solver.EmailBox;
import sudoku.solver.EmailHandler;
import sudoku.solver.mailer.Mailer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

//this comment is only to test, if githubs push functionality works
public class MainApp {
    private static String boxName = "A1";
    private static String boxNameForMqtt;
    private static String initialValues = "00:2, 10:8, 12:7, 21:5, 22:9";
    private static String managerURL = "";
    private static String managerPort = "";
    private static String mqttUrl;
    private static String mqttPort;
    private static String mqttPrefix;

    private static boolean ssl = true;
    private static int emailPollingDelay = 10;
    private static boolean debug = false;

    private static String emailAdress = "sudokusolver2019@gmail.com";
    private static String imapServer = "imap.gmail.com";
    private static int imapPort = 993;
    private static String imapUsername = "sudokusolver2019";
    private static String smtpServer = "smtp.gmail.com";
    private static int smptPort = 465;
    private static String smtpUsername = "sudokusolver2019@gmail.com";
    private static String password = "#sudokuSolver2019";

    public static void main(String[] args) {
        System.out.println("\n - programm start! - \n");
//BasicConfigurator.configure();

        //someCamle();

        //Mailtest begin
        //sudoku.solver.mailer.Mailer mailer = new sudoku.solver.mailer.Mailer();
        //mailer.testMailer();
        //Mailtest end

        /**
         *
         * 1. Send Message to BoxManager (initialize())
         * 2. Initialize Box receive initValues per Mail
         * done!
         */

        //send Initialize-Message
        sudoku.solver.Initializer initializer = new sudoku.solver.Initializer();
        initializer.sendInitialize();

        //send an own-created initialValues-Message
        initializer.sendInitialValues();

        //programm in a break, sleeping or make the following method-call a demon
        //receive the own-created initialValues-Message
        String initValuesAsJSON = initializer.readInitialValuesFromEmailServer(emailAdress,password);

        //process initValueAsJSON
        parseInitialJsonData(initValuesAsJSON);

        //send ready-Message
        try{
            sendReadyMessageToBoxManager();
        }catch (Exception e){
            e.printStackTrace();
        }

        //send own created start-Message
        initializer.sendStartMessage();

        //programm in a break, sleeping or make the following method-call a demon
        // receive own created start-Message
        if(initializer.readInitialValuesFromEmailServer(emailAdress,password).equals("sudoku/start"))
        {
            boolean boolStart = true;
            //the box can start
        }

        //sendInitialRequest
        // parseInitialJsonData(sendInitialRequest());
        EmailBox sudokuBox = new EmailBox(boxName,initialValues);
        EmailHandler emailHandler = new EmailHandler(sudokuBox,emailAdress, imapServer,imapPort,imapUsername,smtpServer,smptPort,smtpUsername,password);
        emailHandler.start();

        /**
         * 3. Start MQTT / Email Routes
         * 4. Send OK TO BoxManager -> done by box via email!
         */
        //TODO create MQTT and Rest to Email route
    }

    static void someCamle(){

        int minutesToRunUntilAutoStop = 10;
        //AbstractApplicationContext sendMailContext = new ClassPathXmlApplicationContext("applicationContext-camel.xml");
        //AbstractApplicationContext receiveMailContext = new ClassPathXmlApplicationContext("applicationContext-receiveEmail.xml");
        Main receiveMailMain = new Main();
        Main sendMailMain = new Main();
        receiveMailMain.addRouteBuilder(new ReceiveMailRoute(boxNameForMqtt,mqttUrl,mqttPort,emailAdress,imapServer,imapPort,ssl,imapUsername,password,emailPollingDelay,debug));
        sendMailMain.addRouteBuilder(new SendMailRoute(boxNameForMqtt,mqttUrl,mqttPort,emailAdress,smtpServer,smptPort,ssl,smtpUsername,password,debug, emailAdress));
        try {
            System.out.println("starting receive");
            receiveMailMain.start();
            System.out.println("started receive");
            System.out.println("starting send");
            sendMailMain.start();
            System.out.println("started send");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //sendMailContext.start();
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
        //sendMailContext.stop();
        //receiveMailMain.stop();
        //sendMailContext.close();
        //receiveMailMain.close();
    }

    private static String sendInitialRequest()  {
        URL myUrl = null;
        HttpURLConnection httpURLConnection = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            myUrl = new URL("http://" + managerURL + ":" + managerPort + "/api/initialize");
             httpURLConnection = (HttpURLConnection) myUrl.openConnection();
            httpURLConnection.setRequestMethod("GET");
            BufferedReader bufferedReaderIn = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

            String line;
            while ((line = bufferedReaderIn.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private static void parseInitialJsonData(String json) throws JSONException {
        JSONObject obj = new JSONObject(json);
        /*mqttUrl = obj.getString("mqtt_ip");
        mqttPort = ""+ obj.getInt("mqtt_port");*/
        mqttUrl = obj.getString("mqtt-ip");
        mqttPort = ""+ obj.getInt("mqtt-port");
        //mqttPrefix = ""+ obj.getInt("mqtt_prefix");
        //boxNameForMqtt = obj.getString("boxname").trim();
        boxNameForMqtt = obj.getString("box").trim();
        boxName = "BOX_" + boxNameForMqtt.substring(boxNameForMqtt.length() - 2, boxNameForMqtt.length()).toUpperCase();
        if (obj.has("init")) {
            JSONArray arr = obj.getJSONArray("init");
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject value = arr.getJSONObject(i);
                for (int j = 0; j < 3; j++) {
                    for (int k = 0; k < 3; k++) {
                        if (value.has("" + j + k)) {
                            b.append(j);
                            b.append(k);
                            b.append(':');
                            b.append(value.getInt("" + j + k));
                            if (i != arr.length() - 1) {
                                b.append(',');
                            }
                        }
                    }
                }
            }
            initialValues = b.toString();
        }
    }

    public static void sendReadyMessageToBoxManager() throws IOException {
        URL myurl = new URL("http://" + managerURL + ":" + managerPort + "/api/ready?" + URLEncoder.encode("box=" + boxNameForMqtt, "UTF-8"));
        HttpURLConnection con = (HttpURLConnection) myurl.openConnection();

        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String line;
        StringBuilder content = new StringBuilder();

        while ((line = in.readLine()) != null) {
        }
    }


















}