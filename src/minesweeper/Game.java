package minesweeper;

import javafx.scene.control.TextField;

public class Game {
    private TextField[][] sweepingArea = new TextField[15][15];

    public Game(TextField[][] sweepingArea){
        this.sweepingArea = sweepingArea;
    }


}
