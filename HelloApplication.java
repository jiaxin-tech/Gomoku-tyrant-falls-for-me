package com.gomoku;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private static final int Size=20;
    private static final int cellSize=30;
    private static final int tolerance=10;


    private int [][]  board=new int[Size][Size];
    public boolean blackturn=true;
    public boolean gameover=false;

    private GraphicsContext gc;
    private Label label;
    @Override
    public void start(Stage stage) throws IOException {
        BorderPane root = new BorderPane();
        Canvas canvas = new Canvas(Size*cellSize,Size*cellSize);
        gc = canvas.getGraphicsContext2D();
        drawboard();

        HBox hbox = new HBox(10);
        Button startButton = new Button("New Game");
        startButton.setOnAction(event -> {
            board = new int[Size][Size];
            blackturn = true;
            gameover = false;
            label.setText("Current Player: Black");
            drawboard();
        });
        label = new Label("Current Player: Black");
        hbox.getChildren().addAll(startButton, label);




        root.setCenter(canvas);
        root.setBottom(hbox);

        stage.setScene(new Scene(root));
        stage.setTitle("Gomoku");
        stage.show();
    }

    private void drawboard() {
        gc.setFill(Color.BURLYWOOD);
        gc.fillRect(0,0,Size*cellSize,Size*cellSize);
        gc.setStroke(Color.BLACK);
        for (int i = 0; i < Size; i++) {
            gc.strokeLine(i*cellSize, 0, i*cellSize, Size*cellSize);
            gc.strokeLine(0, i*cellSize, Size*cellSize, i*cellSize);
        }
        //board[1][1]=1;
        for (int i = 0; i < Size; i++) {
            for (int j = 0; j < Size; j++) {
                if (board[i][j] == 1) {
                    drawStone(i, j, Color.BLACK);
                } else if (board[i][j] == 2) {
                    drawStone(i, j, Color.WHITE);
                }
            }
        }
    }

    private void drawStone(int i, int j, Color color) {
        gc.setFill(color);
        gc.fillOval(i*cellSize-15,j*cellSize-15,cellSize,cellSize);
    }

    public static void main(String[] args) {
        launch();
    }
}