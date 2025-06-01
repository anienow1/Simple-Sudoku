package sudoku;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.util.Duration;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(createUI(stage), 1000, 1000);
        stage.setScene(scene);
        stage.setTitle("Sudoku");
        stage.show();
    }

    private VBox createUI(Stage stage) {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 20;");

        // Timer
        Label timer = new Label("00:00");
        timer.setFont(new Font(30));
        final int[] secondsElapsed = { 0 };

        Timeline timerTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    secondsElapsed[0]++;
                    int minutes = secondsElapsed[0] / 60;
                    int seconds = secondsElapsed[0] % 60;
                    timer.setText(String.format("%02d:%02d", minutes, seconds));
                }));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();

        // Difficulty menu
        ComboBox<String> difficultyMenu = new ComboBox<>();
        difficultyMenu.getItems().addAll("Easy", "Medium", "Hard", "Expert");
        difficultyMenu.setValue("Medium");

        // Create initial board
        final SudokuBoard[] currentBoard = { new SudokuBoard(difficultyMenu.getValue(), true) };

        // New Game Button
        Button newGame = new Button("New Game");
        newGame.setOnAction(e -> {
            String difficulty = difficultyMenu.getValue();
            root.getChildren().removeIf(node -> node instanceof SudokuBoard);
            SudokuBoard newBoard = new SudokuBoard(difficulty, true);
            currentBoard[0] = newBoard;
            root.getChildren().add(2, newBoard); // keep layout order
            secondsElapsed[0] = 0;
        });

        // Hint Button
        Button hint = new Button("Hint");
        hint.setOnAction(e -> currentBoard[0].giveHint());

        // Controls
        HBox controls = new HBox(20, difficultyMenu, newGame);
        controls.setAlignment(Pos.CENTER);

        VBox hintBox = new VBox(hint);
        hintBox.setAlignment(Pos.CENTER);

        // Victory check 
        Timeline victoryPoll = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    if (currentBoard[0].isSolved()) {
                        timerTimeline.stop();
                        showVictoryScreen(stage, secondsElapsed[0], difficultyMenu.getValue());
                    }
                }));
        victoryPoll.setCycleCount(Timeline.INDEFINITE);
        victoryPoll.play();

        root.getChildren().addAll(timer, controls, currentBoard[0], hintBox);
        return root;
    }

    private void showVictoryScreen(Stage stage, int time, String difficulty) {
        VBox victory = new VBox();
        victory.setAlignment(Pos.CENTER);
        victory.setSpacing(20);

        Label victoryLabel = new Label("You solved the puzzle!");
        victoryLabel.setFont(new Font(35));

        Label timeTaken = new Label(String.format("%02d:%02d", time / 60, time % 60));
        timeTaken.setFont(new Font(35));

        Label diffLabel = new Label("Difficulty: " + difficulty);
        diffLabel.setFont(new Font(35));

        Button exitButton = new Button("Exit");
        exitButton.setFont(new Font(35));
        exitButton.setOnAction(ev -> stage.close());

        victory.getChildren().addAll(victoryLabel, timeTaken, diffLabel, exitButton);

        Scene victoryScene = new Scene(victory, 1000, 1000);
        stage.setScene(victoryScene);
    }

    public static void main(String[] args) {
        launch();
    }

}