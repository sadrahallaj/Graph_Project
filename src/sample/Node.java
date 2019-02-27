package sample;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;

public class Node extends Button {
    private int index;
    private boolean isVisited;
    private double centerX;
    private double centerY;
    private String btnText ;
    private Color btnColor ;
    public double getCenterX() {
        return centerX;
    }

    public void setCenterX(double centerX) {
        this.centerX = centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public void setCenterY(double centerY) {
        this.centerY = centerY;
    }

    public Node(int index, double centerX , double centerY){
        this.isVisited = false;
        this.index = index;
        this.centerX = centerX ;
        this.centerY = centerY ;
        this.btnText = String.valueOf(index);
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }

}
