package sudoku;

import java.util.HashSet;
import java.util.Set;

import java.net.http.*;
import java.net.URI;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private Cell[][] solutionBoard = new Cell[SIZE][SIZE];
    public Set<Cell> illegalCells = new HashSet<>();
    public String difficulty;

    public SudokuBoard(String difficulty) {
        this.difficulty = difficulty;
        pullBoardfromSugoku();
        createBoard();
    }

    public Cell[][] createBoard() {
        this.setAlignment(Pos.CENTER);
        this.setGridLinesVisible(true);
        JSONObject grids = pullBoardfromSugoku();
        JSONArray startBoard = grids.getJSONArray("value");
        JSONArray solution = grids.getJSONArray("solution");


        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {

                Cell cell;
                Cell solutionCell = new Cell(row, col, true, this);
                solutionCell.setText(String.valueOf(solution.getJSONArray(row).get(col)));

                int value = (int) startBoard.getJSONArray(row).get(col);


                if (value != 0) {
                    cell = new Cell(row, col, true, this);
                    cell.setText(String.valueOf(value));

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
                solutionBoard[row][col] = solutionCell;
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

    private JSONObject pullBoardfromSugoku() {
        String url = "https://sudoku-api.vercel.app/api/dosuku?level=" + this.difficulty;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Big class called ApiResponse which holds Newboard
            // Newboard holds results (Success of the query), message, and grids
            // Grids holds value (the start board), solution (the end board), and difficulty
            JSONObject json;
            JSONObject grids;
            String diff = "";

            do {
                json = new JSONObject(response.body()).getJSONObject("newboard");
                grids = json.getJSONArray("grids").getJSONObject(0);
                System.out.println(this.difficulty + " " + grids.getString("difficulty"));
                diff = grids.getString("difficulty");
            } while (!diff.equals(this.difficulty));

            return grids;

        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}
