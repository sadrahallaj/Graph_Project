package sample;

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
        this.setStyle("-fx-background-color: #d0d0d0 ;-fx-background-radius: 50 ;");

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

