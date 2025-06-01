package sudoku;

import java.util.ArrayList;

import javafx.scene.control.TextField;

public class Cell extends TextField {
    private final int row;
    private final int col;
    private  boolean isLocked;
    private final SudokuBoard board;

    public Cell(int row, int col, boolean locked, SudokuBoard board) {
        super();
        this.row = row;
        this.col = col;
        this.isLocked = locked;
        this.board = board;

        if (isLocked) {
            setLocked();
        }

        setPrefSize(80, 80);
        setAlignment(javafx.geometry.Pos.CENTER);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isInitial() {
        return isLocked;
    }

    public void setLocked() {
        this.setEditable(false);
        this.isLocked = true;
        this.setStyle("-fx-background-color: lightgray; -fx-font-weight: bold;");
    }

    public void setColor(String color) {
        super.setStyle("-fx-background-color: " + color + ";");
    }

    /**
     * When called on a cell, this function adds any cells that meet "illegal"
     * sudoku standards to the field "board"'s illegalList.
     */
    public void makeHighlights() {

        this.board.illegalCells.remove(this);

        ArrayList<Cell> rowList = rowSafe();
        ArrayList<Cell> colList = colSafe();
        ArrayList<Cell> boxList = boxSafe();

        addIllegalCells(rowList);
        addIllegalCells(colList);
        addIllegalCells(boxList);

        if (!safeToPlace()) {
            this.board.illegalCells.add(this);
        }
    }

    private void addIllegalCells(ArrayList<Cell> cells) {
        for (Cell cell : cells) {
            if (!cell.isInitial() && !this.board.illegalCells.contains(cell)) {
                this.board.illegalCells.add(cell);
            }
        }
    }
    /**
     * Iterates through the row on the sudoku board to find any cells with the same
     * value as the Cell calling the method.
     * 
     * @return An ArrayList of all cells in the row with the same value.
     */
    private ArrayList<Cell> rowSafe() {
        ArrayList<Cell> aList = new ArrayList<>();

        for (Cell aCell : this.board.board[this.getRow()]) {
            if (!aCell.getText().equals("")) {
                if (aCell.getText().equals(this.getText()) && !aCell.equals(this)) {
                    aList.add(aCell);
                }
            }
        }
        return aList;
    }

    /**
     * Iterates through the column on the sudoku board to find any cells with the
     * same value as the Cell calling the method.
     * 
     * @return An ArrayList of all cells in the column with the same value.
     */
    private ArrayList<Cell> colSafe() {
        ArrayList<Cell> aList = new ArrayList<>();

        int colIDX = this.getCol();
        for (Cell[] cells : this.board.board) {
            if (!cells[colIDX].getText().equals("")) {
                if (cells[colIDX].getText().equals(this.getText()) && !cells[colIDX].equals(this)) {
                    aList.add(cells[colIDX]);
                }
            }
        }
        return aList;
    }

    /**
     * Iterates through the 3x3 box on the sudoku board to find any cells with the
     * same value as the Cell calling the method.
     * 
     * @return An ArrayList of all cells in the box with the same value.
     */
    private ArrayList<Cell> boxSafe() {
        ArrayList<Cell> aList = new ArrayList<>();

        int horizontalBox = (this.getCol() / 3) * 3;
        int verticalBox = (this.getRow() / 3) * 3;

        for (int otherCellRow = 0; otherCellRow < 3; otherCellRow++) {
            for (int otherCellCol = 0; otherCellCol < 3; otherCellCol++) {
                Cell otherCell = this.board.board[otherCellRow + verticalBox][otherCellCol + horizontalBox];
                if (!otherCell.getText().equals("")) {
                    if (otherCell.getText().equals(this.getText()) && !otherCell.equals(this)) {
                        aList.add(otherCell);
                    }
                }
            }
        }
        return aList;
    }

    public boolean safeToPlace() {
        return (rowSafe().isEmpty() &&
                colSafe().isEmpty() &&
                boxSafe().isEmpty());
    }
}
