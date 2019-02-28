package sample;

import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.awt.*;

public class Controller {

    public Button btnNewNode;
    public Button btnNewLine;
    public Button btnFinish;
    public Button btnBfs;
    public Button btnDfs;
    public Pane customPane;
    public Label label1;
    private boolean waitingForPlacement = false;
    public int index = 0;

    public void btnNewNodeClicked() {
        waitingForPlacement = true;
        customPane.setOnMouseClicked(event -> {
            double centerX = event.getX();
            double centerY = event.getY();
            if(waitingForPlacement){
                Node node = new Node(index++, centerX, centerY);
                customPane.getChildren().add(node);
                waitingForPlacement = false;
            }
        });
    }
    public void newLineclicked() {
//        LinkedList< LinkedList<Integer> > graph = new LinkedList<>();
//        LinkedList <Integer> Temp= new LinkedList<>();
//        Temp.add(10);
//        graph.get(1).add();
//        graph.add(Temp);
    }

    public void Finishclicked() {
        btnNewNode.setVisible(false);
        btnNewLine.setVisible(false);
        btnFinish.setVisible(false);
        btnDfs.setVisible(true);
        btnBfs.setVisible(true);
    }

    public void bfsclicked() {
    }

    public void dfsclicked() {

    }

}
