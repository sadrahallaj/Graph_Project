package com.graphAlgorithm.view.componenets;

import javafx.scene.control.Button;

import java.io.Serializable;

public class GraphNode extends Button implements Serializable {
    private int index;
    private double DirY;
    private double DirX;

    public double getDirY() {
        return DirY;
    }

    public void setDirY(double dirY) {
        DirY = dirY;
    }

    public double getDirX() {
        return DirX;
    }

    public void setDirX(double dirX) {
        DirX = dirX;
    }

    /**
     * garph node, a button that save the information about each ndoe of graph
     * @param index index og graph
     * @param centerX x parameter
     * @param centerY y parameter
     */
    public GraphNode(int index, double centerX , double centerY){
        super();
        this.index = index;
        this.setText(String.valueOf(index));
        DirX = centerX;
        DirY = centerY;
        this.setLayoutX(centerX);
        this.setLayoutY(centerY);
        this.setStyle("-fx-background-color: #d0d0d0; -fx-font-size: 16; -fx-background-radius: 50 ; -fx-pref-height: 50 ; -fx-pref-width: 50");
    }

    public void copy(){
        this.setLayoutX(DirX);
        this.setLayoutY(DirY);
    }

    /**
     * @return return the index
     */
    public int getIndex() {
        return index;
    }

}
