package sudoku.solver;

import lib.Debugger;

public class EmailBox extends SudokuBox{
    private String managerEmail;

    public EmailBox(String managerEmail){
        this.managerEmail = managerEmail;
        Debugger.__("Initialized email instance", this);
        //init unused fields -> and remove existing afterwards
    }





    //TODO set initial values in manager!

}
