package com.graphAlgorithm.view.componenets;


import javafx.scene.Node;
import javafx.scene.control.Label;

import java.io.Serializable;

/**
 * a serializable label to save in graph
 */
public class LabelSerializable extends Label implements Serializable {
    String text ="";
    private double DirY;
    private double DirX;

    public double getDirX() {
        return DirX;
    }

    public double getDirY() {
        return DirY;
    }

    public void setDirY(double dirY) {
        setLayoutY(dirY);
        DirY = dirY;
    }

    public void setDirX(double dirX) {
        DirX = dirX;
        setLayoutX(dirX);
    }

    public String getLabelText(){
        return text;
    }

    public LabelSerializable(String text) {
        super(text);
        this.text = text;
    }

    public LabelSerializable(String text, Node graphic) {
        super(text, graphic);
    }
}
