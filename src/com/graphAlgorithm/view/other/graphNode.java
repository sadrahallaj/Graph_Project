package com.graphAlgorithm.view.other;

import javafx.scene.control.Button;

import java.io.Serializable;

public class graphNode extends Button implements Serializable {
    private int index;

    /**
     * garph node, a button that save the information about each ndoe of graph
     * @param index index og graph
     * @param centerX x parameter
     * @param centerY y parameter
     */
    public graphNode(int index, double centerX , double centerY){
        super();
        this.index = index;
        this.setText(String.valueOf(index));
        this.setLayoutX(centerX);
        this.setLayoutY(centerY);
        this.setStyle("-fx-background-color: #d0d0d0; -fx-font-size: 16; -fx-background-radius: 50 ; -fx-pref-height: 50 ; -fx-pref-width: 50");
    }

    /**
     * @return return the index
     */
    public int getIndex() {
        return index;
    }

}

