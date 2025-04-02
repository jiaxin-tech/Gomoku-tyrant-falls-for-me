package com.gomoku;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.input.MouseEvent;

import java.util.Stack;

public class test extends Application {
    private static final int SIZE = 20;
    private static final int CELL_SIZE = 35;

    private int[][] board = new int[SIZE][SIZE];
    private boolean blackTurn = true;
    private boolean gameOver = false;
    private int moveCount = 0;
    private int blackMax = 0;
    private int whiteMax = 0;

    private Stack<int[][]> boardHistory = new Stack<>();
    private Stack<int[][]> redoHistory = new Stack<>();

    private GraphicsContext gc;
    private Label currentPlayerLabel;
    private Label countLabel;
    private Label blackLabel;
    private Label whiteLabel;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        StackPane stackPane = new StackPane();
        Canvas canvas = new Canvas(SIZE * CELL_SIZE, SIZE * CELL_SIZE);
        gc = canvas.getGraphicsContext2D();


        Label welcomeLabel = new Label("Welcome to Gomoku!");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 50));
        Button playButton = new Button("Play");
        playButton.setPrefWidth(200);
        playButton.setPrefHeight(50);
        playButton.setOnAction(event -> {
            root.setCenter(stackPane);
            resetGame();
        });

        VBox vbox = new VBox(20);
        vbox.getChildren().addAll(welcomeLabel, playButton);
        vbox.setAlignment(Pos.CENTER);
        root.setCenter(vbox);


        HBox controlBox = new HBox(12);
        Button startButton = new Button("New Game");
        startButton.setOnAction(event -> resetGame());

        Button undoButton = new Button("Undo");
        undoButton.setOnAction(event -> undo());

        Button redoButton = new Button("Redo");
        redoButton.setOnAction(event -> redo());

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> root.setCenter(vbox));

        currentPlayerLabel = new Label("Current Player: Black");
        currentPlayerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30));

        controlBox.getChildren().addAll(startButton, currentPlayerLabel, redoButton, undoButton, exitButton);
        controlBox.setAlignment(Pos.CENTER);


        HBox scoreBox = new HBox(12);
        scoreBox.setAlignment(Pos.CENTER);

        countLabel = new Label("Number of moves: " + moveCount);
        countLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        blackLabel = new Label("Max black score: " + blackMax);
        blackLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        whiteLabel = new Label("Max white score: " + whiteMax);
        whiteLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        scoreBox.getChildren().addAll(countLabel, blackLabel, whiteLabel);
        root.setTop(scoreBox);


        Canvas highlightCanvas = new Canvas(SIZE * CELL_SIZE, SIZE * CELL_SIZE);
        GraphicsContext highlightGC = highlightCanvas.getGraphicsContext2D();
        stackPane.getChildren().addAll(canvas, highlightCanvas);


        highlightCanvas.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            Color highlightColor = Color.YELLOW;
            double highlightRadius = 10;
            highlightGC.clearRect(0, 0, SIZE * CELL_SIZE, SIZE * CELL_SIZE);

            int x = (int)Math.round(event.getX() / CELL_SIZE);
            int y = (int)Math.round(event.getY() / CELL_SIZE);

            if (x >= 0 && x < SIZE && y >= 0 && y < SIZE) {
                if (board[x][y] != 0) {
                    highlightGC.setFill(Color.RED);
                } else {
                    highlightGC.setFill(highlightColor);
                }
                highlightGC.fillOval(x * CELL_SIZE - highlightRadius,
                        y * CELL_SIZE - highlightRadius,
                        highlightRadius * 2, highlightRadius * 2);
            }
        });


        highlightCanvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            canvas.fireEvent(event);
        });
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (!gameOver) {
                handleClick(event.getX(), event.getY());
            }
        });

        root.setBottom(controlBox);

        stage.setScene(new Scene(root, 800, 800));
        stage.setTitle("Gomoku");
        stage.show();

    }

    private void resetGame() {
        board = new int[SIZE][SIZE];
        blackTurn = true;
        gameOver = false;
        moveCount = 0;
        blackMax = 0;
        whiteMax = 0;
        boardHistory.clear();
        redoHistory.clear();

        currentPlayerLabel.setText("Current Player: Black");
        countLabel.setText("Number of moves: " + moveCount);
        blackLabel.setText("Max black score: " + blackMax);
        whiteLabel.setText("Max white score: " + whiteMax);

        drawBoard();
    }

    private void saveBoardState() {
        int[][] copy = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, SIZE);
        }
        boardHistory.push(copy);
        redoHistory.clear();
    }

    private void undo() {
        if (!boardHistory.isEmpty() && !gameOver) {
            int[][] current = new int[SIZE][SIZE];
            for (int i = 0; i < SIZE; i++) {
                System.arraycopy(board[i], 0, current[i], 0, SIZE);
            }
            redoHistory.push(current);


            board = boardHistory.pop();
            blackTurn = !blackTurn;
            moveCount--;

            currentPlayerLabel.setText("Current Player: " + (blackTurn ? "Black" : "White"));
            countLabel.setText("Number of moves: " + moveCount);

            drawBoard();
        }
    }

    private void redo() {
        if (!redoHistory.isEmpty() && !gameOver) {
            int[][] current = new int[SIZE][SIZE];
            for (int i = 0; i < SIZE; i++) {
                System.arraycopy(board[i], 0, current[i], 0, SIZE);
            }
            boardHistory.push(current);

            board = redoHistory.pop();
            blackTurn = !blackTurn;
            moveCount++;

            currentPlayerLabel.setText("Current Player: " + (blackTurn ? "Black" : "White"));
            countLabel.setText("Number of moves: " + moveCount);

            drawBoard();
        }
    }

    private void handleClick(double x, double y) {
        int clickedX = (int)Math.round(x / CELL_SIZE);
        int clickedY = (int)Math.round(y / CELL_SIZE);

        if (clickedX >= 0 && clickedX < SIZE &&
                clickedY >= 0 && clickedY < SIZE &&
                board[clickedX][clickedY] == 0) {

            saveBoardState();
            board[clickedX][clickedY] = blackTurn ? 1 : 2;
            drawStone(clickedX, clickedY, blackTurn ? Color.BLACK : Color.WHITE);
            moveCount++;
            countLabel.setText("Number of moves: " + moveCount);

            if (checkWin(clickedX, clickedY)) {
                gameOver = true;
                currentPlayerLabel.setText("Game Over - " + (blackTurn ? "Black" : "White") + " wins!");
            } else {
                blackTurn = !blackTurn;
                currentPlayerLabel.setText("Current Player: " + (blackTurn ? "Black" : "White"));
            }
        }
    }

    private boolean checkWin(int x, int y) {
        int current = board[x][y];
        return checkDirection(x, y, current, 0, 1) ||  // Vertical
                checkDirection(x, y, current, 1, 0) ||  // Horizontal
                checkDirection(x, y, current, 1, 1) ||  // Diagonal \
                checkDirection(x, y, current, -1, 1);   // Diagonal /
    }

    private boolean checkDirection(int x, int y, int current, int dx, int dy) {
        int count = 1;

        for (int i = 1; i < 5; i++) {
            int nx = x + dx * i;
            int ny = y + dy * i;
            if (nx < 0 || nx >= SIZE || ny < 0 || ny >= SIZE || board[nx][ny] != current) {
                break;
            }
            count++;
        }


        for (int i = 1; i < 5; i++) {
            int nx = x - dx * i;
            int ny = y - dy * i;
            if (nx < 0 || nx >= SIZE || ny < 0 || ny >= SIZE || board[nx][ny] != current) {
                break;
            }
            count++;
        }

            if (current == 1 && count > blackMax) {
                blackMax = count;
                blackLabel.setText("Max black score: " + blackMax);
            } else if (current == 2 && count > whiteMax) {
                whiteMax = count;
                whiteLabel.setText("Max white score: " + whiteMax);
        }
        return count == 5;
    }

    private void drawBoard() {
        gc.setFill(Color.BURLYWOOD);
        gc.fillRect(0, 0, SIZE * CELL_SIZE, SIZE * CELL_SIZE);
        gc.setStroke(Color.BLACK);
        for (int i = 0; i < SIZE; i++) {
            gc.strokeLine(i * CELL_SIZE, 0, i * CELL_SIZE, SIZE * CELL_SIZE);
            gc.strokeLine(0, i * CELL_SIZE, SIZE * CELL_SIZE, i * CELL_SIZE);
        }

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 1) {
                    gc.setFill(Color.BLACK);
                    gc.fillOval(i * CELL_SIZE - CELL_SIZE / 2.0,
                            j * CELL_SIZE - CELL_SIZE / 2.0,
                            CELL_SIZE, CELL_SIZE);
                } else if (board[i][j] == 2) {
                    gc.setFill(Color.WHITE);
                    gc.fillOval(i * CELL_SIZE - CELL_SIZE / 2.0,
                            j * CELL_SIZE - CELL_SIZE / 2.0,
                            CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }

    private void drawStone(int i, int j, Color color) {
        Timeline timeline = new Timeline();
        for (int c = 1; c <= 10; c++) {
            double opacity = c / 10.0;
            KeyFrame keyFrame = new KeyFrame(Duration.millis(c * 50), event -> {
                gc.setFill(Color.color(color.getRed(), color.getGreen(), color.getBlue(), opacity));
                gc.fillOval(i * CELL_SIZE - CELL_SIZE / 2.0,
                        j * CELL_SIZE - CELL_SIZE / 2.0,
                        CELL_SIZE, CELL_SIZE);
            });
            timeline.getKeyFrames().add(keyFrame);
        }
        timeline.setCycleCount(1);
        timeline.play();
    }

    public static void main(String[] args) {
        launch();
    }
}