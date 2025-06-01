package sudoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
    private String[][] solutionBoard = new String[SIZE][SIZE];
    public Set<Cell> illegalCells = new HashSet<>();
    public String difficulty;

    public SudokuBoard(String difficulty, boolean local) {

        this.difficulty = difficulty;
        if (local) {
            createLocalBoard();
        } else {
            createOnlineBoard();
        }
    }

    public Cell[][] createOnlineBoard() {
        this.setAlignment(Pos.CENTER);
        this.setGridLinesVisible(true);
        JSONObject grids = pullBoardfromSugoku();
        JSONArray startBoard = grids.getJSONArray("value");
        JSONArray solution = grids.getJSONArray("solution");

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {

                int value = (int) startBoard.getJSONArray(row).get(col);
                boolean isLocked = value != 0;
                String valueString = isLocked ? String.valueOf(value) : "";

                Cell cell = createCell(row, col, isLocked, valueString);

                board[row][col] = cell;
                solutionBoard[row][col] = String.valueOf(solution.getJSONArray(row).get(col));
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
            if (this.isSolved()) {

            }
        });
    }

    private void validateBoard() {
        illegalCells.clear();
        for (Cell[] row : board) {
            for (Cell cell : row) {
                if (!cell.isInitial()) {
                    cell.setColor("white");
                    if (!cell.getText().isEmpty()) {
                        cell.makeHighlights();
                    }
                }
            }
        }

        for (Cell cell : illegalCells) {
            cell.setColor("red");
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
        return illegalCells.isEmpty();
    }

    private JSONObject pullBoardfromSugoku() {
        String url = "https://sudoku-api.vercel.app/api/dosuku?level=" + this.difficulty;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            String diff = "";
            JSONObject grids;
            

            do {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                JSONObject json = new JSONObject(response.body()).getJSONObject("newboard");
                grids = json.getJSONArray("grids").getJSONObject(0);

                diff = grids.getString("difficulty");
            } while (!diff.equals(this.difficulty));

            return grids;

        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    private void createLocalBoard() {
        this.setAlignment(Pos.CENTER);
        this.setGridLinesVisible(true);

        //Initialize all cells
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                board[row][col] = new Cell(row, col, false, this);
                this.add(board[row][col], col, row);
            }
        }

        fillBoard(this.board);

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                String value = board[row][col].getText();
                this.solutionBoard[row][col] = value;

                Cell cell = createCell(row, col, false, value);
                board[row][col] = cell;
                this.add(cell, col, row);
                
            }
        }

        switch (this.difficulty) {
            case ("Expert"):
                removeCells(this, 55); // 55
                break;
            case ("Hard"):
                removeCells(this, 50); // 50
                break;
            case ("Medium"):
                removeCells(this, 45); // 45
                break;
            case ("Easy"):
                removeCells(this, 38); // 38
                break;
        }
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (!board[row][col].getText().equals("")) {
                    board[row][col].setLocked();
                }
            }
        }
    }

    private Cell createCell(int row, int col, boolean isLocked, String value) {
        Cell cell = new Cell(row, col, isLocked, this);
        if (value != null && !value.isEmpty()) {
            cell.setText(value);
            if (isLocked)
                cell.setLocked();
        }

        BorderStrokeStyle style = BorderStrokeStyle.SOLID;
        BorderWidths widths = new BorderWidths(
                row % 3 == 0 ? 2 : 1,
                col % 3 == 2 ? 2 : 1,
                row % 3 == 2 ? 2 : 1,
                col % 3 == 0 ? 2 : 1);
        cell.setBorder(new Border(new BorderStroke(Color.BLACK, style, CornerRadii.EMPTY, widths)));
        addInputConstraints(cell, row, col);
        return cell;
    }

    private static boolean fillBoard(Cell[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                Cell cell = board[row][col];
                if (cell.getText().equals("")) {
                    List<Integer> numbers = IntStream.rangeClosed(1, 9)
                            .boxed()
                            .collect(Collectors.toList());
                    Collections.shuffle(numbers);

                    for (int num : numbers) {
                        cell.setText(String.valueOf(num));
                        if (cell.safeToPlace()) {
                            if (fillBoard(board)) {
                                return true;
                            }
                        } else {
                            cell.setText("");
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean removeCells(SudokuBoard board, int numToRemove) {
        List<Cell> cellList = new ArrayList<>();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cellList.add(board.board[row][col]);
            }
        }

        Collections.shuffle(cellList);

        int numRemoved = 0;

        while (!cellList.isEmpty() && numRemoved < numToRemove) {
            
            Cell cell = cellList.remove(0);
            String value = cell.getText();
            cell.setText("");

            if (hasUniqueSolution(board.board)) {
                numRemoved++;
            } else {
                cell.setText(value);
            }
        }

        return true;
    }

    private static int countSolutions(int[][] grid, int row, int col) { // Uses backtracking to find the amount of
                                                                        // solutions that a board has
        if (row == 9)
            return 1;
        if (col == 9)
            return countSolutions(grid, row + 1, 0);
        if (grid[row][col] != 0) {
            return countSolutions(grid, row, col + 1);
        }

        int count = 0;

        for (int num = 1; num <= 9; num++) {
            if (isSafe(grid, row, col, num)) {
                grid[row][col] = num;
                count += countSolutions(grid, row, col + 1);
                grid[row][col] = 0;

                if (count > 1)
                    return count;
            }
        }

        return count;
    }

    public static boolean hasUniqueSolution(Cell[][] board) { // Function to start the recursive backtracking call
        int[][] grid = toIntGrid(board);
        return countSolutions(grid, 0, 0) == 1;
    }

    private static int[][] toIntGrid(Cell[][] board) {
        int[][] grid = new int[9][9];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                String text = board[r][c].getText();
                grid[r][c] = text.isEmpty() ? 0 : Integer.parseInt(text);
            }
        }
        return grid;
    }

    private static boolean isSafe(int[][] grid, int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
            if (grid[row][i] == num || grid[i][col] == num)
                return false;
        }
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (grid[boxRow + r][boxCol + c] == num)
                    return false;
            }
        }
        return true;
    }

    private boolean isEmpty(Cell cell){
        return cell.getText().isEmpty();
    }

    public void giveHint() {
        ArrayList<Cell> emptyCells = new ArrayList<>();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (isEmpty(board[row][col])) {
                    emptyCells.add(board[row][col]);
                }
            }
        }

        if (!emptyCells.isEmpty()) {
            Collections.shuffle(emptyCells);
            Cell hintCell = emptyCells.get(0);
            hintCell.setText(solutionBoard[hintCell.getRow()][hintCell.getCol()]);
            hintCell.setLocked();
            hintCell.setColor("green");
            validateBoard();
        }
    }

}
