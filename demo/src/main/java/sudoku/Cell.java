package sudoku;

import javafx.scene.control.TextField;

public class Cell extends TextField {
    private final int row;
    private final int col;
    private final boolean isLocked;
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

        setPrefSize(50, 50);
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

    private void setLocked() {
        this.setEditable(false);
        this.setStyle("-fx-background-color: lightgray; -fx-font-weight: bold;");
    }

    public void setColor(String color) {
        super.setStyle("-fx-background-color: " + color + ";");
    }

    

    public boolean isLegalPosition(Cell cell) {

        boolean isLegal = true;
        String cellValue = cell.getText();

        this.board.illegalCells.remove(cell);

        try {

            for (int otherCellCol = 0; otherCellCol < 9; otherCellCol++) { // Check for horizontal multiples

                Cell otherCell = this.board.board[this.row][otherCellCol];

                if (!otherCell.getText().equals("")) {
                    if (otherCell.getText().equals(cellValue) && !cell.equals(otherCell)) { // Highlight illegal boxes
                                                                                            // red
                        cell.setStyle("-fx-background-color: red;");
                        if (!otherCell.isInitial()) {
                            otherCell.setStyle("-fx-background-color: red;");
                        }

                        this.board.illegalCells.add(cell);
                        this.board.illegalCells.add(otherCell);

                        isLegal = false;
                    }
                }
            }

            for (int otherCellRow = 0; otherCellRow < 9; otherCellRow++) { // Check for vertical multiples

                Cell otherCell = this.board.board[otherCellRow][this.col];

                if (!otherCell.getText().equals("")) {
                    if (otherCell.getText().equals(cellValue) && !cell.equals(otherCell)) { // Highlight them red
                        cell.setStyle("-fx-background-color: red;");
                        if (!otherCell.isInitial()) {
                            otherCell.setStyle("-fx-background-color: red;");
                        }
                        this.board.illegalCells.add(cell);
                        this.board.illegalCells.add(otherCell);

                        isLegal = false;
                    }
                }
            }

            // Check the 3x3 box that the tile is in

            int horizontalBox = this.col / 3;
            int verticalBox = this.row / 3;

            for (int otherCellRow = 0; otherCellRow < 3; otherCellRow++) {
                for (int otherCellCol = 0; otherCellCol < 3; otherCellCol++) {

                    Cell otherCell = this.board.board[verticalBox * 3 + otherCellRow][horizontalBox * 3 + otherCellCol];

                    if (!otherCell.getText().equals("")) {
                        if (otherCell.getText().equals(cellValue) && !cell.equals(otherCell)) { // Highlight them red
                            cell.setStyle("-fx-background-color: red;");
                            if (!otherCell.isInitial()) {
                                otherCell.setStyle("-fx-background-color: red;");
                            }
                            this.board.illegalCells.add(cell);
                            this.board.illegalCells.add(otherCell);

                            isLegal = false;
                        }
                    }
                }
            }

        } catch (java.lang.NumberFormatException e) {
            System.out.println(e);
            return false;
        }

        if (isLegal) {
            cell.setStyle("-fx-background-color: white;"); // If the input box is changed, unhighlight it
        }

        return isLegal;
    }

}
