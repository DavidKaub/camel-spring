package sudoku.solver.mailer;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.*;
import javax.mail.Store;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.*;
import java.io.*;
import javax.mail.Flags.Flag;
import javax.mail.search.FlagTerm;
import javax.mail.internet.MimeMultipart;

public class Mailer {
    //Declare recipient's & sender's e-mail id.
    private String destmailid;
    private String sendrmailid;

    //Mention user name and password as per your configuration
    private String uname;
    private String pwd;

    //We are using relay.jangosmtp.net for sending emails
    private String smtphost;

    public void testMailer() {
        final Mailer mailer = new Mailer();
        mailer.sendGmail("http:/<SpringBoxManager>/api/initialize");
        mailer.sendGmail("http:/<SpringBoxManager>/api/ready?box=<sudoku/box_a1");
        mailer.sendGmail("{\"box\":\"sudoku/box_a1\",\"result\":[2,3,1,4,7,6,9,8,5]}");
//        try {
//            TimeUnit.SECONDS.sleep(20);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        mailer.receive("sudokusolver2019@gmail.com", "#sudokuSolver2019");
    }

    public List<String> receive(String receiver, String password) {
        final String email_id = receiver;
        final String sPassword = password;

        //set properties
        Properties properties = new Properties();
        //You can use imap or imaps , *s -Secured
        properties.put("mail.store.protocol", "imaps");
        //Host Address of Your Mail
        properties.put("mail.imaps.host", "imap.gmail.com");
        //Port number of your Mail Host
        properties.put("mail.imaps.port", "993");
        //properties.put("mail.imaps.timeout", "10000");

        try {
            //create a session
            Session session = Session.getDefaultInstance(properties, null);
            //SET the store for IMAPS
            Store store = session.getStore("imaps");
            System.out.println("Connection initiated");
            //Trying to connect IMAP server
            store.connect(email_id, sPassword);
            System.out.println("Connection is ready");

            //Get inbox folder
            Folder inbox = store.getFolder("inbox");
            //SET readonly format (*You can set read and write)
            inbox.open(Folder.READ_WRITE);

            //inbox.open(Folder.READ_WRITE);
/*            MimeMultipart message = (MimeMultipart) inbox.getMessage(1).getContent();
            MimeMultipart message2 = (MimeMultipart) inbox.getMessage(2).getContent(); */
            //inbox.close(false);

            //Display email Details
            Message messages[] = inbox.search(new FlagTerm(new Flags(Flag.SEEN), false));

            /* Use a suitable FetchProfile    */
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(FetchProfile.Item.CONTENT_INFO);
            inbox.fetch(messages, fp);
            List<String> contents = new ArrayList<String>();
            try
            {
                for (int i = 0; i < messages.length; i++)
                {
                    //this method prints out the message number, then prints the Envelope
                    System.out.println("MESSAGE #" + (i + 1) + ":");
                    //This method prints out the messages sender, receiver, subject, receivedDate and its content
                    Address[] a;
                    // FROM
                    if ((a = messages[i].getFrom()) != null)
                    {
                        for (int j = 0; j < a.length; j++)
                        {
                            System.out.println("FROM: " + a[j].toString());
                        }
                    }
                    // TO
                    if ((a = messages[i].getRecipients(Message.RecipientType.TO)) != null)
                    {
                        for (int j = 0; j < a.length; j++)
                        {
                            System.out.println("TO: " + a[j].toString());
                        }
                    }
                    String subject = messages[i].getSubject();
                    Date receivedDate = messages[i].getReceivedDate();

                    System.out.println("Subject : " + subject);
                    System.out.println("Received Date : " + receivedDate.toString());
                    if(messages[i] != null){
                        contents.add(messages[i].getContent().toString());
                    }
                }
                return contents;
            }
            catch (Exception ex)
            {
                System.out.println("Exception arise at the time of read mail");
                ex.printStackTrace();
            }
            finally
            {
                inbox.close(true);
                store.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendGmail(String message) {
        sendGmail(message, "From Box To MQTT");
    }

    public void sendGmail(String message, String subject) {
        /*        //mail to own mailserver
        mailer.setDestmailid("email1@localhost");
        mailer.setSendrmailid("email1@localhost");
        mailer.setUname("email1");
        mailer.setPwd("apfelmusmann");
        mailer.setSmtphost("136.199.13.190");

        //Set properties and their values
        Properties propvls = new Properties();
        propvls.put("mail.smtp.auth", "true");
        propvls.put("mail.smtp.starttls.enable", "true");
        propvls.put("mail.smtp.host", mailer.smtphost);
        propvls.put("mail.smtp.port", "587");*/

        //mail to gmail
        setDestmailid("sudokusolver2019@gmail.com");
        setSendrmailid("sudokusolver2019@gmail.com");
        setUname("sudokusolver2019");
        setPwd("#sudokuSolver2019");
        //mailer.setSmtphost("136.199.13.190");
        setSmtphost("smtp.googlemail.com");

        //Set properties and their values
        Properties propvls = new Properties();
        propvls.put("mail.smtp.auth", "true");
        //propvls.put("mail.smtp.ssl.trust", "*");
        propvls.put("mail.smtp.starttls.enable", "true");
        propvls.put("mail.smtp.host", smtphost);
        propvls.put("mail.smtp.port", "587");

        //Create a Session object & authenticate uid and pwd
        Session sessionobj = Session.getInstance(propvls,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(getUname(), getPwd());
                    }
                });

        try {
            //Create MimeMessage object & set values
            Message messageobj = new MimeMessage(sessionobj);
            messageobj.setRecipients(Message.RecipientType.TO,InternetAddress.parse(getDestmailid()));
            messageobj.setFrom(new InternetAddress(getSendrmailid()));
            messageobj.setSubject(subject);
            messageobj.setText(message);
            //Now send the message
            Transport.send(messageobj);
            System.out.println("Your email sent successfully....");
        } catch (MessagingException exp) {
            throw new RuntimeException(exp);
        }
    }

    private void printAllMessages(Message[] msgs) throws Exception
    {
        for (int i = 0; i < msgs.length; i++)
        {
            //this method prints out the message number, then prints the Envelope
            System.out.println("MESSAGE #" + (i + 1) + ":");
            printEnvelope(msgs[i]);
        }
    }

    private void printEnvelope(Message message) throws Exception
    {
        //This method prints out the messages sender, receiver, subject, receivedDate and its content
        Address[] a;
        // FROM
        if ((a = message.getFrom()) != null)
        {
            for (int j = 0; j < a.length; j++)
            {
                System.out.println("FROM: " + a[j].toString());
            }
        }
        // TO
        if ((a = message.getRecipients(Message.RecipientType.TO)) != null)
        {
            for (int j = 0; j < a.length; j++)
            {
                System.out.println("TO: " + a[j].toString());
            }
        }
        String subject = message.getSubject();
        Date receivedDate = message.getReceivedDate();
        String content = message.getContent().toString();
        System.out.println("Subject : " + subject);
        System.out.println("Received Date : " + receivedDate.toString());
        System.out.println("Content : " + content);
        //getContent(message);
    }

    private void dumpPart(Part p) throws Exception
    {
        // Dump input stream ..
        InputStream is = p.getInputStream();
        // If "is" is not already buffered, wrap a BufferedInputStream
        // around it.
        if (!(is instanceof BufferedInputStream))
        {
            is = new BufferedInputStream(is);
        }
        int c;
        System.out.println("Message : ");
        while ((c = is.read()) != -1)
        {
            System.out.write(c);
        }
    }

    public String getDestmailid() {
        return destmailid;
    }

    public void setDestmailid(String destmailid) {
        this.destmailid = destmailid;
    }

    public String getSendrmailid() {
        return sendrmailid;
    }

    public void setSendrmailid(String sendrmailid) {
        this.sendrmailid = sendrmailid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getSmtphost() {
        return smtphost;
    }

    public void setSmtphost(String smtphost) {
        this.smtphost = smtphost;
    }
}