package minesweeper;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.InterruptedIOException;
import java.util.Random;

public class Gui extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Minesweeper");
        GridPane root = new GridPane();
        this.initContent(root);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // -------------------------------------------------------------------------
    private final int M = 15;
    private final int N = 15;

    private final TextField[][] sweepingArea = new TextField[M][N];
    private final Label[] infoTextArea = new Label[3];
    private final int tileSize = 30;
    private final int width = 450;
    private final int height = 500;
    private Boolean first = true;
    private int bombs = 0;
    private final Font boldFont = Font.font("Verdana",FontWeight.BOLD,16);
    private final String[] combinations = {"Bombs: " + bombs,"Minesweeper","Time: "};
    private final Label[] lblCombinations = new Label[combinations.length];

    private final int xTiles = width / tileSize;
    private final int yTiles = height / tileSize;
    private final Random random = new Random();

    private void initContent(GridPane pane) {
        pane.setPadding(new Insets(5));
        // set horizontal gap between components
        pane.setHgap(2);
        // set vertical gap between components
        pane.setVgap(2);
        pane.setPrefSize(width,height);

        // INFO PANE

        GridPane infoPane = new GridPane();
        pane.add(infoPane,0,1);
        pane.setPadding(new Insets(0));
        infoPane.setBackground(new Background(new BackgroundFill(Color.color(74/255.,117/255.,44/255.), CornerRadii.EMPTY,Insets.EMPTY)));
        pane.setHgap(0);
        pane.setVgap(0);
        for (int i = 0; i < infoTextArea.length; i++) {
            lblCombinations[i] = new Label(combinations[i]);
            infoPane.add(lblCombinations[i],i,0);
            GridPane.setHalignment(lblCombinations[i],HPos.CENTER);
            lblCombinations[i].setStyle("-fx-text-fill: #fff");

            infoTextArea[i] = new Label();
            infoPane.add(infoTextArea[i],i,0);
            infoTextArea[i].setPrefSize(width/3.,50);

            if (i == 1){
                lblCombinations[i].setFont(boldFont);
            }
        }

        // SWEEP PANE

        GridPane SweepPane = new GridPane();
        pane.add(SweepPane, 0, 2);
        SweepPane.setPadding(new Insets(0));
        SweepPane.setVgap(0);
        SweepPane.setHgap(0);

        for (int x = 0; x < sweepingArea.length; x++) {
            for (int y = 0; y < sweepingArea[x].length; y++) {
                sweepingArea[x][y] = new TextField();
                SweepPane.add(sweepingArea[x][y],x,y);
                sweepingArea[x][y].setPrefSize(tileSize,tileSize);
                sweepingArea[x][y].setText("");
                sweepingArea[x][y].setEditable(false);
                sweepingArea[x][y].setAlignment(Pos.CENTER);
                sweepingArea[x][y].setFocusTraversable(false);
                sweepingArea[x][y].setAccessibleText("0 " + x + " " + y + " 0");
                sweepingArea[x][y].setOnMouseClicked(event ->
                {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        leftMouseClicked(event);
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        rightMouseClicked(event);
                    }
                });
                sweepingArea[x][y].setStyle("-fx-text-fill: #fff;");
            }
        }

        for (int i = 0; i < sweepingArea.length; i++) {
            for (int j = 0; j < sweepingArea[i].length; j++) {
                if((i + j) % 2 == 0){
                    sweepingArea[i][j].setBackground(new Background(new BackgroundFill(Color.color(170/255.,215/255.,80/255.), CornerRadii.EMPTY,Insets.EMPTY)));
                } else {
                    sweepingArea[i][j].setBackground(new Background(new BackgroundFill(Color.color(162/255.,209/255.,72/255.), CornerRadii.EMPTY,Insets.EMPTY)));
                }
            }
        }

    }

    // -------------------------------------------------------------------------

    public void btnInit(){
        for (int x = 0; x < sweepingArea.length; x++) {
            for (int y = 0; y < sweepingArea[x].length; y++) {
                int number = 0;
                if(random.nextInt(10) == 1) {
                    number = -1;
                }
                sweepingArea[x][y].setAccessibleText(number + " " + x + " " + y + " 0");
            }
        }
        for (int X = 0; X < sweepingArea.length; X++) {
            for (int Y = 0; Y < sweepingArea[X].length; Y++) {
                int number = Integer.parseInt(sweepingArea[X][Y].getAccessibleText().split(" ")[0]);

                if (number == -1){
                    bombs++;
                    for (int xOffset = -1; xOffset <= 1; xOffset++){
                        for (int yOffset = -1; yOffset <= 1; yOffset++) {

                            if (X + xOffset < 0 || X + xOffset > 14 || Y + yOffset < 0 || Y + yOffset > 14) continue;

                            int mineArea = Integer.parseInt(sweepingArea[X + xOffset][Y + yOffset].getAccessibleText().split(" ")[0]);

                            if (mineArea != -1) {
                                sweepingArea[X + xOffset][Y + yOffset].setAccessibleText((mineArea + 1) + " " + (X + xOffset) + " " + (Y + yOffset) + " 0");
                            }
                        }
                    }
                }
            }
        }
        updateInfo();
    }

    public void updateInfo(){
        lblCombinations[0].setText("Bombs: " + bombs);
    }

    public void steppedOnBomb(){
        System.out.println("You're dead");
    }

    public void fieldColor(int bomb, int x,int y){
        if (bomb == -1) {
            steppedOnBomb();
            sweepingArea[x][y].setText("B");

            if((x + y) % 2 == 0) {
                sweepingArea[x][y].setBackground(new Background(new BackgroundFill(Color.color(210/255.,105/255.,30/255.), CornerRadii.EMPTY,Insets.EMPTY)));
            } else {
                sweepingArea[x][y].setBackground(new Background(new BackgroundFill(Color.color(240/255.,100/255.,0/255.), CornerRadii.EMPTY,Insets.EMPTY)));
            }
        } else if (bomb == 0) {
            if((x + y) % 2 == 0) {
                sweepingArea[x][y].setBackground(new Background(new BackgroundFill(Color.color(236/255.,212/255.,187/255.), CornerRadii.EMPTY,Insets.EMPTY)));
            } else {
                sweepingArea[x][y].setBackground(new Background(new BackgroundFill(Color.color(239/255.,218/255.,197/255.), CornerRadii.EMPTY,Insets.EMPTY)));
            }
        } else {
            if((x + y) % 2 == 0) {
                sweepingArea[x][y].setBackground(new Background(new BackgroundFill(Color.color(229/255.,194/255.,159/255.), CornerRadii.EMPTY,Insets.EMPTY)));
            } else {
                sweepingArea[x][y].setBackground(new Background(new BackgroundFill(Color.color(215/255.,184/255.,153/255.), CornerRadii.EMPTY,Insets.EMPTY)));
            }
        }
    }

    public void leftMouseClicked(MouseEvent event) {
        TextField clickedValue = (TextField) event.getSource();
        if (first){
            first = false;
            btnInit();
        }
        if (clickedValue.getText() != ""){
            return;
        } else {
            String[] mineArea = clickedValue.getAccessibleText().split(" ");
            int x    = Integer.parseInt(mineArea[1]);
            int y    = Integer.parseInt(mineArea[2]);
            floodFillUtil(x,y);
        }
    }

    public void rightMouseClicked(MouseEvent event){
        TextField clickedValue = (TextField) event.getSource();
        if (first){
            return;
        }
        if (clickedValue.getText() != ""){
            return;
        } else {
            bombs--;
            clickedValue.setText("F");
            clickedValue.setBackground(new Background(new BackgroundFill(Color.color(207/255.,0/255.,15/255.), CornerRadii.EMPTY,Insets.EMPTY)));
            updateInfo();
        }
    }

    void floodFillUtil(int x, int y) {
        if (x < 0 || x >= M || y < 0 || y >= N) {
            return;
        }
        if (sweepingArea[x][y].getAccessibleText().split(" ")[3].equals("1")) {
            return;
        }
        String bomb = sweepingArea[x][y].getAccessibleText().split(" ")[0];

        if(!sweepingArea[x][y].getAccessibleText().split(" ")[0].equals("0")) {
            sweepingArea[x][y].setAccessibleText(bomb + " " + x + " " + y + " 1");
            sweepingArea[x][y].setText(bomb);
            fieldColor(Integer.parseInt(bomb),x,y);
            return;
        }

        sweepingArea[x][y].setAccessibleText("0 " + x + " " + y + " 1");
        fieldColor(Integer.parseInt(bomb),x,y);

        floodFillUtil(x + 1, y);
        floodFillUtil(x - 1, y);
        floodFillUtil(x, y + 1);
        floodFillUtil(x, y - 1);
    }
}