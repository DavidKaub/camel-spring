package sudoku.solver;




import java.util.Arrays;
import java.util.StringTokenizer;

public class EmailBox extends SudokuBox{

    public EmailBox(String boxName, String initialValues){
        this.boxName = boxName;
        unusedValues.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        StringTokenizer stringTokenizer = new StringTokenizer(boxName, "_");
        String boxColRow = stringTokenizer.nextToken();
        if(boxColRow.equals("box")){
            boxColRow = stringTokenizer.nextToken();
        }
        column = boxColRow.charAt(0);
        column = Character.toUpperCase(column);
        row = Integer.parseInt("" + boxColRow.charAt(1));
        setInitialValues(initialValues);
    }


    public void setNetworkHandler(EmailHandler emailHandler){
        this.networkHandler = emailHandler;
    }



}
