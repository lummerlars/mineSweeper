package minesweeper;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

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

    private final TextField[][] sweepingArea = new TextField[15][15];
    private TextField clickedValue = new TextField();
    private final Label[] infoTextArea = new Label[3];
    private static final int tileSize = 30;
    private static final int width = 450;
    private static final int height = 500;
    private Boolean first = true;
    private int flags = 0;
    private final Font boldFont = Font.font("Verdana",FontWeight.BOLD,16);
    private String[] combinations = {"Flags: " + flags,"Minesweeper","Time: "};
    private Label[] lblCombinations = new Label[combinations.length];

    private static int M = 15;
    private static int N = 15;

    private static final int xTiles = width / tileSize;
    private static final int yTiles = height / tileSize;
    private Random random = new Random();
    Game game1 = new Game(sweepingArea);

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
        pane.setPadding(new Insets(10));
        pane.setHgap(0);
        pane.setVgap(0);

        for (int i = 0; i < infoTextArea.length; i++) {
            lblCombinations[i] = new Label(combinations[i]);
            infoPane.add(lblCombinations[i],i,0);
            GridPane.setHalignment(lblCombinations[i],HPos.CENTER);

            infoTextArea[i] = new Label();
            infoPane.add(infoTextArea[i],i,0);
            infoTextArea[i].setPrefSize(width/3,50);

            if (i == 1){
                lblCombinations[i].setFont(boldFont);
            }
        }

        // SWEEP PANE

        GridPane SweepPane = new GridPane();
        pane.add(SweepPane, 0, 2);
        SweepPane.setPadding(new Insets(10));
        SweepPane.setVgap(0);
        SweepPane.setHgap(0);

        for (int i = 0; i < sweepingArea.length; i++) {
            for (int j = 0; j < sweepingArea[i].length; j++) {
                sweepingArea[i][j] = new TextField();
                SweepPane.add(sweepingArea[i][j],i,j);
                sweepingArea[i][j].setPrefSize(tileSize,tileSize);
                sweepingArea[i][j].setText("0");
                sweepingArea[i][j].setEditable(false);
                sweepingArea[i][j].setAlignment(Pos.CENTER);
                sweepingArea[i][j].setFocusTraversable(false);
                sweepingArea[i][j].setAccessibleText("0" + " " + i + " " + j);
                sweepingArea[i][j].setOnMouseClicked(event -> this.mouseClicked(event));
            }
        }

        for (int i = 0; i < sweepingArea.length; i++) {
            for (int j = 0; j < sweepingArea[i].length; j++) {
                if((i + j) % 2 == 0){
                    sweepingArea[i][j].setBackground(new Background(new BackgroundFill(Color.color(170/255.,215/255.,80/255.), CornerRadii.EMPTY,Insets.EMPTY)));
                    sweepingArea[i][j].setStyle("-fx-text-fill: #AAD750FF;");
                } else {
                    sweepingArea[i][j].setBackground(new Background(new BackgroundFill(Color.color(162/255.,209/255.,72/255.), CornerRadii.EMPTY,Insets.EMPTY)));
                    sweepingArea[i][j].setStyle("-fx-text-fill: #A2D148FF;");
                }
            }
        }

    }

    // -------------------------------------------------------------------------

    public void btnInit(){
        for (int i = 0; i < sweepingArea.length; i++) {
            for (int j = 0; j < sweepingArea[i].length; j++) {
                sweepingArea[i][j].setAccessibleText("" + random.nextInt(7) + " " + i + " " + j);
            }
        }
        for (int i = 0; i < sweepingArea.length; i++) {
            for (int j = 0; j < sweepingArea[i].length; j++) {
                String[] mineField = sweepingArea[i][j].getAccessibleText().split(" ");
                if (Integer.parseInt(mineField[0]) == 1){
                    sweepingArea[i][j].setText("");
                    for (int x = -1; x <= 1; x++){
                        for (int y = -1; y <= 1; y++) {
                            if (i + x < 0 || i + x > 14 || j + y < 0 || j + y > 14) continue;
                            String[] mineArea = sweepingArea[i+x][j+y].getAccessibleText().split(" ");
                            if (Integer.parseInt(mineArea[0]) != 1){
                                sweepingArea[i+x][j+y].setText("" + (Integer.parseInt(sweepingArea[i+x][j+y].getText()) + 1));
                            } else {
                                //sweepingArea[i+x][j+y].setBackground(new Background(new BackgroundFill(Color.color(255/255.,0/255.,0/255.), CornerRadii.EMPTY,Insets.EMPTY)));
                                //sweepingArea[i+x][j+y].setText("");
                                flags++;
                            }
                        }
                    }
                }
            }
        }
        updateInfo();
    }

    public void updateInfo(){
        lblCombinations[0].setText("Flags: " + flags);
    }

    public void fieldColor(){
        String[] mineArea = clickedValue.getAccessibleText().split(" ");
        if (Integer.parseInt(mineArea[0]) != 1){
            if((Integer.parseInt(mineArea[1]) + Integer.parseInt(mineArea[2])) % 2 == 0){
                clickedValue.setBackground(new Background(new BackgroundFill(Color.color(229/255.,194/255.,159/255.), CornerRadii.EMPTY,Insets.EMPTY)));
            } else {
                clickedValue.setBackground(new Background(new BackgroundFill(Color.color(215/255.,184/255.,153/255.), CornerRadii.EMPTY,Insets.EMPTY)));
            }
        } else if (Integer.parseInt(mineArea[0]) == 1){
            clickedValue.setText("B");
            if((Integer.parseInt(mineArea[1]) + Integer.parseInt(mineArea[2])) % 2 == 0){
                clickedValue.setBackground(new Background(new BackgroundFill(Color.color(210/255.,105/255.,30/255.), CornerRadii.EMPTY,Insets.EMPTY)));
            } else {
                clickedValue.setBackground(new Background(new BackgroundFill(Color.color(240/255.,100/255.,0/255.), CornerRadii.EMPTY,Insets.EMPTY)));
            }
        }
    }

    public void mouseClicked(MouseEvent event){
        clickedValue = (TextField) event.getSource();
        clickedValue.setStyle("-fx-text-fill: #fff;");
        if (first){
            first = false;
            btnInit();
        }
        fieldColor();
        TextField x = new TextField("1");
        TextField y = new TextField("1");
        TextField newC = new TextField("9");

        floodFill(sweepingArea,x,y,newC);

        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                //Her skal der være kode for FloodFill.
            }

        }
    }

    static void floodFillUtil(TextField mineArea[][], int x, int y, TextField prevC, TextField newC){
        if (x < 0 || x >= M || y < 0 || y >= N){
            return;
        }
        if (mineArea[x][y] != prevC){
            return;
        }
        mineArea[x][y] = newC;

        floodFillUtil(mineArea, x+1, y, prevC, newC);
        floodFillUtil(mineArea, x-1, y, prevC, newC);
        floodFillUtil(mineArea, x, y+1, prevC, newC);
        floodFillUtil(mineArea, x, y-1, prevC, newC);
    }

    static void floodFill(TextField mineArea[][], TextField x, TextField y, TextField newC){
        TextField prevC = mineArea[Integer.parseInt(x.getText())][Integer.parseInt(y.getText())];
        if (prevC == newC) return;

        floodFillUtil(mineArea, Integer.parseInt(x.getText()), Integer.parseInt(y.getText()), prevC, newC);
    }


}