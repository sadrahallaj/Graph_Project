package com.graphAlgorithm.view.other;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;

public class Node extends Button {
    private int index;
    private boolean isVisited;

    public Node(int index, double centerX , double centerY){
        super();
        this.isVisited = false;
        this.index = index;
        this.setText(String.valueOf(index));
        this.setLayoutX(centerX);
        this.setLayoutY(centerY);
        this.setStyle("-fx-background-color: #d0d0d0; -fx-font-size: 16; -fx-background-radius: 50 ; -fx-pref-height: 50 ; -fx-pref-width: 50");
    }

    public int getIndex() {
        return index;
    }

}

