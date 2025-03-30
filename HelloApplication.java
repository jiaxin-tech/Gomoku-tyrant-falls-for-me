package com.gomoku;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

import java.io.IOException;

public class HelloApplication extends Application {
    private static final int Size=20;
    private static final int cellSize=35;


    private int [][]  board=new int[Size][Size];
    public boolean blackturn=true;
    public boolean gameover=false;
    public int count=0;
    public int blackmax=0;
    public int whitemax=0;

    private GraphicsContext gc;
    private Label label;
    @Override
    public void start(Stage stage) throws IOException {
        BorderPane root = new BorderPane();
        StackPane stackpane = new StackPane();
        Canvas canvas = new Canvas(Size*cellSize,Size*cellSize);
        gc = canvas.getGraphicsContext2D();

        Label welcomelable = new Label("Welcome to Gomoku!");
        welcomelable.setFont(Font.font("Arial", FontWeight.BOLD,50));
        Button playButton = new Button("Play");
        playButton.setPrefWidth(200);
        playButton.setPrefHeight(50);
        playButton.setOnAction(event -> {
            root.setCenter(stackpane);
            board = new int[Size][Size];
            blackturn = true;
            gameover = false;
            label.setText("Current Player: Black");
            drawboard();
        });
        VBox vbox = new VBox();
        vbox.getChildren().addAll(welcomelable,playButton);
        vbox.setAlignment(Pos.CENTER);
        root.setCenter(vbox);


        HBox hbox = new HBox(12);
        Button startButton = new Button("New Game");
        startButton.setOnAction(event -> {
            board = new int[Size][Size];
            blackturn = true;
            gameover = false;
            label.setText("Current Player: Black");
            drawboard();
        });
        Button undoButton = new Button("Undo");
        undoButton.setOnAction(event -> {
            undo();
        });
        Button redoButton = new Button("Redo");
        redoButton.setOnAction(event -> {
            redo();
        });
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> {
            root.setCenter(vbox);
        });
        label = new Label("Current Player: Black");
        label.setFont(Font.font("Arial", FontWeight.BOLD,30));
        hbox.getChildren().addAll(startButton, label, redoButton, undoButton,exitButton);
        hbox.setAlignment(Pos.CENTER);

        HBox hbox2 = new HBox(12);
        hbox2.setAlignment(Pos.CENTER);
        Label countlabel = new Label("Number of movements:"+count);
        countlabel.setFont(Font.font("Arial", FontWeight.BOLD,24));
        Label black = new Label("Max black score:"+blackmax);
        black.setFont(Font.font("Arial", FontWeight.BOLD,24));
        Label white = new Label("Max white score:"+whitemax);
        white.setFont(Font.font("Arial", FontWeight.BOLD,24));
        hbox2.getChildren().addAll(countlabel,black,white);
        root.setTop(hbox2);

        Canvas usercanvas = new Canvas(Size*cellSize,Size*cellSize);
        GraphicsContext gc2 = usercanvas.getGraphicsContext2D();
        stackpane.getChildren().addAll(canvas,usercanvas);

        int [][] highlightpoint=new int[Size*Size][2];
        for(int i=0; i<Size; i++){
            for(int j=0; j<Size; j++){
                highlightpoint[i*Size+j][0]=i*cellSize;
                highlightpoint[i*Size+j][1]=j*cellSize;
            }
        }
        usercanvas.addEventHandler(MouseEvent.MOUSE_MOVED,event -> {
            Color highlightColor = Color.YELLOW;
            double highlightRadius = 10;
            gc2.clearRect(0,0,Size*cellSize,Size*cellSize);
            for (int[] point : highlightpoint) {
                int x = point[0];
                int y = point[1];
                if (Math.hypot(event.getX() - x, event.getY() - y) <= highlightRadius) {
                    gc2.setFill(highlightColor);
                    gc2.fillOval(x - highlightRadius, y - highlightRadius, highlightRadius * 2, highlightRadius * 2);
                }

                int oordinatex= (int) Math.round(event.getX()/cellSize);
                int oordinatey= (int) Math.round(event.getY()/cellSize);
                if(board[oordinatex][oordinatey]!=0){
                    gc2.setFill(Color.RED);
                    gc2.fillOval(oordinatex*cellSize- highlightRadius, oordinatey*cellSize- highlightRadius, highlightRadius * 2, highlightRadius * 2);
                }
            }
        });
        usercanvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            canvas.fireEvent(event);
        });

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
           if(!gameover) {
               handleClick(event.getX(),event.getY());
           }

        });
        root.setBottom(hbox);

        stage.setScene(new Scene(root,800,800));
        stage.setTitle("Gomoku");
        stage.show();
    }

    private void redo() {
    }

    private void undo() {
    }

    private void handleClick(double x, double y) {
        int clickedX = (int)Math.round(x/cellSize);
        int clickedY = (int)Math.round(y/cellSize);
        if(board[clickedX][clickedY]==0) {
            board[clickedX][clickedY]=blackturn ? 1 : 2;
            drawStone(clickedX,clickedY,blackturn? Color.BLACK : Color.WHITE);
            count++;

            if(checkwin(clickedX,clickedY)) {
                gameover = true;
                label.setText("Game Over"+"     "+(blackturn? "Black" : "White")+" "+"win!");
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
        FadeTransition boardfadein=new FadeTransition(Duration.seconds(3), gc.getCanvas());
        boardfadein.setFromValue(0.0);
        boardfadein.setToValue(1.0);
        boardfadein.setCycleCount(1);
        boardfadein.setAutoReverse(false);
        boardfadein.play();
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
        Timeline timeline = new Timeline();
        for(int c=1;c<10;c++){
            double opacity=c/10.0;
            KeyFrame keyFrame = new KeyFrame(Duration.millis(c*100), event -> {
                gc.setFill(Color.color(color.getRed(),color.getGreen(),color.getBlue(),opacity));
                gc.fillOval(i*cellSize- (double) cellSize /2,j*cellSize- (double) cellSize /2,cellSize,cellSize);
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