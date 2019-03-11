package sudoku.solver;



import sudoku.lib.MyDebugger;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

abstract class SudokuBox {

    protected String boxName;
    protected char column;
    protected int row;

    protected List<String> neighborNames = new ArrayList<>();
    protected List<Integer> unusedValues = new ArrayList<>();

    protected NetworkHandler networkHandler;
    protected SudokuCell[][] boxCells;
    protected boolean isSolved = false;



    public SudokuBox() {
        initializeCells();
        MyDebugger.__("initialized!", this);
        MyDebugger.__(this.toString(), this);
        fireLocalUpdate();
        sendInitialState();
    }




    public void fireLocalUpdate() {
        if (!isSolved) {
            /**
             * integrated internal solving:
             * count the amount of all possible fits of each cell.
             * If a number is only counted once -> the cell can be solved
             */

            for (int i = 0; i < boxCells.length; i++) {
                for (int j = 0; j < boxCells[i].length; j++) {
                    SudokuCell cell = boxCells[i][j];
                    if (cell.isSolved() && unusedValues.contains(cell.getValue())) {
                        removeAvailableValueFromBox(cell.getValue());
                    }
                }
            }

            if (unusedValues.size() == 1) {
                /**
                 * if only one value is not used that can be easily set to the (last) unsolved cell.
                 * should actually not occur because the constraints should have been propagated and the
                 * cell should have solved itself
                 */

                for (int i = 0; i < boxCells.length; i++) {
                    for (int j = 0; j < boxCells[i].length; j++) {
                        SudokuCell cell = boxCells[i][j];
                        if (!cell.isSolved()) {
                            cell.setValue(unusedValues.get(0));
                        }
                    }
                }
            }
            if (unusedValues.size() == 0) {
                MyDebugger.__(getBoxName() + " is solved!", this);
                this.isSolved = true;
            }
        }
    }


    public void receiveCellUpdate(SudokuCell cell) {
        if (!isSolved) {
            MyDebugger.__("local update received", this);
            if (cell.isSolved() && unusedValues.contains(cell.getValue())) {
                MyDebugger.__(boxName + " found new value at cell " + cell.getGlobalCellName() + " value = " + cell.getValue(), this);
                removeAvailableValueFromBox(cell.getValue());
                /**
                 * informiere alle zellen die noch nicht geloesst sind darüber, dass der wert nicht mehr verfügbar ist
                 */
                fireLocalUpdate();
                sendNewKnowledgeToNeighbors(cell);
                MyDebugger.__(this.toString(), this);
            }
        }
    }

    private void sendNewKnowledgeToNeighbors(SudokuCell cell) {
        String message = cell.getGlobalCellName() + ":" + cell.getValue();
        sendNewKnowledgeToNeighbors(message);
    }

    private void sendNewKnowledgeToNeighbors(String message) {
        MyDebugger.__("Sending new value to all neighbors: " + message, this);
        networkHandler.addOutgoingMessage(message);
    }

    private void removeAvailableValueFromBox(int value) {
        if (!isSolved && unusedValues.contains(value)) {
            unusedValues.remove((Integer) value);
            addConstraintToAllCells(value);
        }
    }

    private void addConstraintToAllCells(int value) {
        MyDebugger.__("adding constraint " + value + " to all cells", this);
        for (int i = 0; i < boxCells.length; i++) {
            for (int j = 0; j < boxCells[i].length; j++) {
                boxCells[i][j].addNewConstraint(value);
            }
        }
    }


    public void sendInitialState() {
        /**
         * Propagate current state to all neighbors
         * 1. Laufe über alle Zellen
         * Wenn solved = true sende Wissen
         */
        fireLocalUpdate();
        for (int i = 0; i < boxCells.length; i++) {
            for (int j = 0; j < boxCells[i].length; j++) {
                if (boxCells[i][j].isSolved()) {
                    sendNewKnowledgeToNeighbors(boxCells[i][j]);
                }
            }
        }
    }


    public void receiveKnowledge(String message) {
        if (isSolved) {
            return;
        }
        if (message.contains("BOX_")) {
            //then the message has relative information and has to be converted
            if (CellChecker.checkRelativeKnwoledge(message)) {
                message = convertRelativeToAbsoluteKnowledge(message);
                receiveAbsoluteKnowledge(message);
            } else {
                throw new IllegalArgumentException("Invalid Message received");
            }
        } else {
            receiveAbsoluteKnowledge(message);
        }
    }

    private void receiveAbsoluteKnowledge(String message) {
        if (isSolved) {
            return;
        }
        //message with absolute information
        if (!CellChecker.checkAbsoluteKnowledge(message)) {
            Exception exception = new IllegalArgumentException("INVALID KNOWLEDGE RECEIVED");
        } else {
            MyDebugger.__("receiving new knowledge: " + message, this);

            StringTokenizer strokenizer = new StringTokenizer(message, ":");
            String cell = strokenizer.nextToken();

            char column = cell.charAt(0);
            int row = Integer.parseInt("" + cell.charAt(1));
            int value = Integer.parseInt(strokenizer.nextToken());

            boolean forCol = false;


            //TODO Pürfe diese Methoden bzw die schleifen!
            if (checkIfColumnIsWithinBounderies(column)) {
                MyDebugger.__("knowledge is relevant for column", this);
                forCol = true;
                int locCol = column - this.column;
                for (int i = 0; i < boxCells[locCol].length; i++) {
                    boxCells[locCol][i].addNewConstraint(value);
                }
            }
            boolean forRow = false;

            if (checkIfRowIsWithinBounderies(row)) {
                MyDebugger.__("knowledge is relevant for row", this);
                forRow = true;
                int locRow = row - this.row;
                //System.out.println("cell: "+cell+ " - loc row= "+ locRow);
                for (int i = 0; i < boxCells.length; i++) {
                    boxCells[i][locRow].addNewConstraint(value);
                }
            }
            if (!(forCol || forRow)) {
                MyDebugger.__("new knowledge not used - not relevant!", this);
            }

            fireLocalUpdate();
            /**
             * Send to all neighbors and safe as already sent!
             */
            sendNewKnowledgeToNeighbors(message);
        }
    }

    private String convertRelativeToAbsoluteKnowledge(String knowledge) {
        throw new IllegalArgumentException("not implemented yet!");
        /**
         * TODO !!!
         */
        //return knowledge;
    }


    private boolean checkIfRowIsWithinBounderies(int row) {
        if (this.row <= row && (this.row + 2) >= row) {
            //System.out.println("check for row: input = "+ row+ " local = "+ this.row+ " -> true");
            return true;
        }
        //System.out.println("check for row: input = "+ row+ " local = "+ this.row+ " -> false");
        return false;
    }

    private boolean checkIfColumnIsWithinBounderies(char column) {
        if (this.column <= column && (this.column + 2) >= column) {
            return true;
        }
        return false;
    }


    private void initializeCells() {
        //setup raw cells
        boxCells = new SudokuCell[3][3];
        for (int i = 0; i < boxCells.length; i++) {
            for (int j = 0; j < boxCells[i].length; j++) {
                boxCells[i][j] = new SudokuCell(this, i, j);
            }
        }
    }


    protected void setInitialValues(String initialValues) {
        StringTokenizer stringTokenizer = new StringTokenizer(initialValues, ", :");

        while (stringTokenizer.hasMoreTokens()) {
            String cell = stringTokenizer.nextToken().trim();
            int value = Integer.parseInt(stringTokenizer.nextToken().trim());
            if (cell.length() != 2) {
                System.out.println("FEHLER!!!");
                throw new IllegalArgumentException("WRONG STRING SPLIT");
            }
            int x = Integer.parseInt("" + cell.charAt(0));
            int y = Integer.parseInt("" + cell.charAt(1));
            MyDebugger.__("Initial Value for cell " + boxCells[x][y].getGlobalCellName() + "  = " + value, this);
            boxCells[x][y].setValue(value);
        }
    }


    public List<String> getNeighborNames() {

        if (!neighborNames.isEmpty()) {
            return neighborNames;
        }
        if (this.column > 'A') {
            /**
             * hat einen linken Nachbarn
             */
            int charVal = Character.valueOf(column);
            charVal = charVal - 3;
            char c = (char) charVal;
            neighborNames.add("BOX_" + c + row);
        }
        if (this.column < 'G') {
            /**
             * hat einen rechten Nachbarn
             */
            int charVal = Character.valueOf(column);
            charVal = charVal + 3;
            char c = (char) charVal;
            neighborNames.add("BOX_" + c + row);
        }
        if (this.row > 1) {
            /**
             * hat einen oberen Nachbarn
             */
            int r = row - 3;
            neighborNames.add("BOX_" + column + r);
        }

        if (this.row < 7) {
            /**
             * hat einen unteren Nachbarn
             */
            int r = row + 3;
            neighborNames.add("BOX_" + column + r);
        }
        return neighborNames;
    }

    public char getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public String getBoxName() {
        return boxName;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getBoxName() + ":");

        stringBuilder.append("\n");
        for (int i = 0; i < boxCells.length; i++) {
            stringBuilder.append("\n -------------");
            stringBuilder.append("\n | ");
            for (int j = 0; j < boxCells[i].length; j++) {
                if (boxCells[j][i].getValue() != -1) {
                    stringBuilder.append(boxCells[j][i].getValue() + " | ");
                } else stringBuilder.append(" " + " | ");
            }
        }
        stringBuilder.append("\n -------------");
        return stringBuilder.toString();
    }


    public boolean isSolved() {
        return isSolved;
    }

    public String printResult() {
        if (!isSolved) return null;
        StringBuilder stringBuilder = new StringBuilder();

        for (int r = 0; r < boxCells[0].length; r++) {
            for (int c = 0; c < boxCells.length; c++) {
                stringBuilder.append(boxCells[c][r].getValue() + ",");
            }
        }
        String result = stringBuilder.toString();
        //remove last comma
        result = result.substring(0, (result.length() - 1));
        return result;
    }

}
