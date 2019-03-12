package sudoku.solver.mailer;

//Emilio planning: This class can
public class MailManager {
    public void test(String[] args) {
        Mailer mailer = new Mailer();
        mailer.testMailer();
        //mailer.send("subject", "message"); sends subject & message from our own mailServer email1 to our own mailServer email1
        //mailer.send("subject", "message", "sth@gmail.com", "password", "server", "host"); send subject & message from sth@gmail.com to itself using the given password
    }

    //Emilio: This Method sends an email to ask the sudokuManager for this box's name and initial values
    public void sendInitializeMessage()
    {

    }

    //Emilio: This Method sends an email to say, that this box is ready to work for the sudokuManager
    public void sendReadyMessage()
    {

    }

    //Emilio: This Method sends an email to say, that this box solved a new cell
    public void sendUpdate()
    {

    }

    //Emilio: This Method sends an email to share its final solved values
    public void sendResult()
    {

    }

    //Emilio: This Method asks for new updates via mail
    public String getUpdates()
    {
        String update = "someUpdate";
        return update;
    }
}
