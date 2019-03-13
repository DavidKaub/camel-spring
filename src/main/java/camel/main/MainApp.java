package camel.main;
import camel.route.MailToMqttRoute;
import camel.route.MqttToMailRoute;
import org.apache.camel.main.Main;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sudoku.solver.EmailBox;
import sudoku.solver.EmailHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

//this comment is only to test, if githubs push functionality works
public class MainApp {
    private static String boxName = "BOX_D7";//TODO remove value
    private static String boxNameForMqtt = "sudoku/box_d7";
    private static String initialValues = "";//Todo remove value 00:7, 10:3, 11:4, 12:6, 22:1
    private static String managerURL = "136.199.12.246";
    private static String managerPort = "4242";
    private static String mqttUrl = "136.199.51.71";
    private static String mqttPort = "1883";
    private static String mqttPrefix = "TEST";




    private static boolean emailSslEnabled ;
    private static int emailPollingDelay = 1000;
    private static boolean emailDebugEnabled = true;
    private static String emailAdressMqttToBox;
    private static String emailAdressBoxToMqtt;
    private static String imapServer;
    private static int imapPort;//gmail
    private static String imapUsernameMqttToBox;
    private static String imapUsernameBoxToMqtt;
    private static String smtpServer;
    private static int smptPort = 465;//gamil
    private static String smtpUsernameMqttToBox;
    private static String smtpUsernameBoxToMqtt;
    private static String emailPasswordMqttToBox;
    private static String emailPasswordBoxToMqtt;

    public static void main(String[] args) {
        System.out.println("\n - programm start! - \n");
        initMail(true);
//BasicConfigurator.configure();



        //Mailtest begin
        //sudoku.solver.mailer.Mailer mailer = new sudoku.solver.mailer.Mailer();
        //mailer.testMailer();
        //Mailtest end



        /**
         *
         * 1. Send Message to BoxManager
         * 2. Initialize Box
         * done!
         */
        //String result = sendInitialRequest();
        System.out.println("connect completed - now parsing data");
        //parseInitialJsonData(result);
        System.out.println("parsed!");
        EmailBox sudokuBox = new EmailBox(boxName,initialValues);
        EmailHandler emailHandler = new EmailHandler(sudokuBox,emailAdressBoxToMqtt, imapServer,imapPort,imapUsernameBoxToMqtt,smtpServer,smptPort,smtpUsernameBoxToMqtt,emailPasswordBoxToMqtt, emailSslEnabled,emailAdressMqttToBox);

        startCamel(emailHandler);
        //TODO verzögern bis start nachricht erhalten
        emailHandler.start();

        try {

            sendReadyMessageToBoxManager();
        } catch (IOException e) {
            e.printStackTrace();
        }



        /**
         * 3. Start MQTT / Email Routes
         * 4. Send OK TO BoxManager -> done by box via email!
         */
        //TODO create MQTT and Rest to Email route
    }




    private static void initMail(boolean gmail){


        if(gmail){
            emailSslEnabled = true;

            imapServer = "imap.gmail.com";
            imapPort = 993;
            smtpServer = "smtp.gmail.com";
            smptPort = 587;//oder 465?


            emailAdressMqttToBox = "sudokusolver2019@gmail.com";
            imapUsernameMqttToBox = "sudokusolver2019";
            smtpUsernameMqttToBox = emailAdressMqttToBox;
            emailPasswordMqttToBox = "#sudokuSolver2019";

            emailAdressBoxToMqtt = "sudokusolver2019v2@gmail.com";
            imapUsernameBoxToMqtt = "sudokusolver2019v2";
            smtpUsernameBoxToMqtt = emailAdressBoxToMqtt;
            emailPasswordBoxToMqtt = "#sudokuSolver2019";



        }else{
            emailSslEnabled = false;

            imapServer = "localhost";
            smtpServer = imapServer;
            imapPort = 143;//gmail
            smptPort = 587;//gamil

            emailAdressMqttToBox = "email1@localhost";
            imapUsernameMqttToBox = "email1";
            smtpUsernameMqttToBox = emailAdressMqttToBox;
            emailPasswordMqttToBox = "apfelmusmann";

            emailAdressBoxToMqtt = "email2@localhost";
            imapUsernameBoxToMqtt = "email2";
            smtpUsernameBoxToMqtt = emailAdressBoxToMqtt;
            emailPasswordBoxToMqtt = "apfelmusmann";
        }

    }


    private static void startCamel(EmailHandler emailHandler){

        int minutesToRunUntilAutoStop = 10;
        //AbstractApplicationContext sendMailContext = new ClassPathXmlApplicationContext("applicationContext-camel.xml");
        //AbstractApplicationContext receiveMailContext = new ClassPathXmlApplicationContext("applicationContext-receiveEmail.xml");
        Main mqttToMailMain = new Main();
        mqttToMailMain.addRouteBuilder(new MqttToMailRoute(boxName,boxNameForMqtt,mqttUrl,mqttPort,mqttPrefix,emailAdressMqttToBox,smtpServer,smptPort,emailSslEnabled,smtpUsernameMqttToBox,emailPasswordMqttToBox,emailDebugEnabled, emailAdressBoxToMqtt, emailHandler,imapUsernameMqttToBox,imapServer,imapPort, emailPollingDelay));

        try {

            System.out.println("starting mqttToMail");
            mqttToMailMain.start();
            System.out.println("started mqttToMail");

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Application context started");

    }



    private static String sendInitialRequest()  {
        URL myUrl = null;
        HttpURLConnection httpURLConnection = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            myUrl = new URL("http://" + managerURL + ":" + managerPort + "/api/initialize");
            //System.out.println("send request to: "+myUrl.toString());
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
        String response = stringBuilder.toString();
        System.out.println("response = "+response);
        return response;
    }







    private static String  parseInitialJsonData(String json) throws JSONException {
        StringBuilder b = new StringBuilder();
        JSONObject obj = new JSONObject(json);
        mqttUrl = obj.getString("mqtt_ip");
        mqttPort = ""+obj.getInt("mqtt_port");
        mqttPrefix = obj.getString("mqtt_prefix");
        boxNameForMqtt = obj.getString("boxname").trim();
        boxName = "BOX_" + boxNameForMqtt.substring(boxNameForMqtt.length() - 2, boxNameForMqtt.length()).toUpperCase();
        if (obj.has("init")) {
            JSONArray arr = (new JSONObject("{\"init\" : " + obj.getString("init") + '}')).getJSONArray("init");

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

        }
        return b.toString();
    }


    public static void sendReadyMessageToBoxManager() throws IOException {
        System.out.println("Sending ready");
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