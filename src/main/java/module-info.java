module sudoku {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens sudoku to javafx.fxml;
    exports sudoku;
}
