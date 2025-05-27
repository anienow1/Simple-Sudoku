package sudoku;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        VBox root = new VBox();
        root.setSpacing(15);
        root.setAlignment(Pos.CENTER);
        

        // Make an instance of a board
        SudokuBoard board = new SudokuBoard("Medium");

        //Reset button 
        Button newGame = new Button("New Game");

        //Diffuculty menu
        ComboBox<String> difficultyMenu = new ComboBox<>();
        difficultyMenu.getItems().addAll("Easy", "Medium", "Hard");
        difficultyMenu.setValue("Medium");

        HBox controls = new HBox(20, difficultyMenu, newGame);
        controls.setAlignment(Pos.CENTER);

        newGame.setOnAction(e -> {
            String difficulty = difficultyMenu.getValue();

            // Delete the old board
            root.getChildren().removeIf(node -> node instanceof SudokuBoard);

            //Make a new board
            SudokuBoard newBoard = new SudokuBoard(difficulty);
            root.getChildren().add(newBoard);
        });

        root.getChildren().addAll(controls, board);

        // scene = new Scene(loadFXML("primary"), 640, 480);
        scene = new Scene(root, 1000, 1000);
        stage.setScene(scene);
        stage.setTitle("Sudoku");
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}