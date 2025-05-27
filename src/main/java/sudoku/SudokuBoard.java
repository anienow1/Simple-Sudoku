package sudoku;

import java.util.HashSet;
import java.util.Set;

//import java.net.http.*;
import java.net.URI;
//import com.google.gson.Gson;

import javafx.geometry.Pos;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class SudokuBoard extends GridPane {
    private static final int SIZE = 9;
    public Cell[][] board = new Cell[SIZE][SIZE];
    public Set<Cell> illegalCells = new HashSet<>();
    public String difficulty;

    public SudokuBoard(String difficulty) {
        this.difficulty = difficulty;
        createBoard();
    }

    public Cell[][] createBoard() {
        this.setAlignment(Pos.CENTER);
        this.setGridLinesVisible(true);

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {

                Cell cell;

                if (Math.random() > .8) {
                    cell = new Cell(row, col, true, this);
                    cell.setText(String.valueOf(((int) ((Math.random() * 9) + 1))));

                } else {
                    cell = new Cell(row, col, false, this);
                }

                BorderStrokeStyle style = BorderStrokeStyle.SOLID;
                BorderWidths widths = new BorderWidths( // Make the 3x3 little boxes
                        row % 3 == 0 ? 2 : 1, // Top
                        col % 3 == 2 ? 2 : 1, // Left
                        row % 3 == 2 ? 2 : 1, // Bottom
                        col % 3 == 0 ? 2 : 1 // Right
                );

                cell.setBorder(new Border(new BorderStroke(Color.BLACK, style, CornerRadii.EMPTY, widths)));

                addInputConstraints(cell, row, col);

                board[row][col] = cell;
                this.add(cell, col, row);
            }
        }
        return board;
    }

    private void addInputConstraints(Cell cell, int rowIDX, int colIDX) { // Check to see if the entered value is a
                                                                          // valid move
        cell.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[1-9]?")) {
                cell.setText(oldVal);
            }

            validateBoard();

        });
    }

    private void validateBoard() {
        illegalCells.clear();
        for (Cell[] row : board) {
            for (Cell cell : row) {
                if (!cell.isInitial()) {
                    cell.setColor("white");
                }
            }
        }

        for (Cell[] row : board) {
            for (Cell cell : row) {
                if (!cell.isInitial()) {
                    cell.isLegalPosition(cell);
                }
            }
        }
        System.out.println(illegalCells.size());
    }

    public boolean isSolved() {
        for (Cell[] row : board) {
            for (Cell cell : row) {
                if (cell.getText().equals("")) {
                    return false;
                }
            }
        }
        if (illegalCells.size() != 0) {
            return false;
        }
        return true;
    }

    private void pullBoard(){

    }
}
