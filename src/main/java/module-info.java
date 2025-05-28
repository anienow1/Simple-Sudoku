module sudoku {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires java.net.http;
    requires com.google.gson;
    requires org.json;

    opens sudoku to javafx.fxml;
    exports sudoku;
}
