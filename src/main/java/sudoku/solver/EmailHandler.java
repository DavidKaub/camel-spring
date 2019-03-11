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
    void establishConnectionToManager() {
        //means in this case connect to email server!

    }

    @Override
    void sendIsSolved() {

    }

    @Override
    void sendPendingMessages() {

    }
}
