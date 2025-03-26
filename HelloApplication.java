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
    private static final int cellSize=35;


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

        HBox hbox = new HBox(12);
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

        canvas.setOnMouseClicked(event -> {
           if(!gameover) {
               handleClick(event.getX(),event.getY());
           }

        });
        root.setCenter(canvas);
        root.setBottom(hbox);

        stage.setScene(new Scene(root));
        stage.setTitle("Gomoku");
        stage.show();
    }

    private void handleClick(double x, double y) {
        int clickedX = (int)Math.round(x/cellSize);
        int clickedY = (int)Math.round(y/cellSize);
        if(board[clickedX][clickedY]==0) {
            board[clickedX][clickedY]=blackturn ? 1 : 2;
            drawStone(clickedX,clickedY,blackturn? Color.BLACK : Color.WHITE);

            if(checkwin(clickedX,clickedY)) {
                gameover = true;
                label.setText("Game Over"+" "+(blackturn? "Black" : "White")+" "+"win!");
            }else {
                blackturn=!blackturn;
                label.setText("Current Player:"+(blackturn? "Black" : "White"));
            }

        }
    }

    private boolean checkwin(int clickedX, int clickedY) {
        int current=board[clickedX][clickedY];
        return chekedirection(clickedX, clickedY, current, 0, 1) ||
                chekedirection(clickedX, clickedY, current, 1, 0) ||
                chekedirection(clickedX, clickedY, current, 1, 1) ||
                chekedirection(clickedX, clickedY, current, -1, 1);
    }

    private boolean chekedirection(int clickedX, int clickedY, int current, int i, int i1) {
        int count=1;
                for(int c=1;c<5;c++){
                int x=clickedX+i*c;
                int y=clickedY+i1*c;
                if(x>20||x<0||y>20||y<0||board[x][y]!=current) {
                    break;
                }else {
                    count=count+1;
                }
                }
        for(int c=1;c<5;c++){
            int x=clickedX-i*c;
            int y=clickedY-i1*c;
            if(x>20||x<0||y>20||y<0||board[x][y]!=current) {
                break;
            }else {
                count=count+1;
            }
        }
        return count == 5;
    }

    private void drawboard() {
        gc.setFill(Color.BURLYWOOD);
        gc.fillRect(0,0,Size*cellSize,Size*cellSize);
        gc.setStroke(Color.BLACK);
        for (int i = 0; i < Size; i++) {
            gc.strokeLine(i*cellSize, 0, i*cellSize, Size*cellSize);
            gc.strokeLine(0, i*cellSize, Size*cellSize, i*cellSize);
        }

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
        gc.fillOval(i*cellSize- (double) cellSize /2,j*cellSize- (double) cellSize /2,cellSize,cellSize);
    }

    public static void main(String[] args) {
        launch();
    }
}