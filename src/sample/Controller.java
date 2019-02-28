package sample;

import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.awt.*;
import java.util.LinkedList;

public class Controller {

    public Button btnNewNode;
    public Button btnNewLine;
    public Button btnFinish;
    public Button btnBfs;
    public Button btnDfs;
    public Pane customPane;
    public Label label1;
    public boolean waitingForPlacement = false;
    public int index = 0;
    public LinkedList<Node> nodeLine = new LinkedList<>();
    public LinkedList< LinkedList<Integer> > nodesList = new LinkedList<>();


    public void btnNewNodeClicked() {
        waitingForPlacement = true;
        customPane.setOnMouseClicked(event -> {
            double centerX = event.getX();
            double centerY = event.getY();
            if(waitingForPlacement){
                Node node = new Node(index, centerX, centerY);
                node.setOnMouseClicked(event1 -> {
                    node.setStyle("-fx-background-color: red ;-fx-background-radius: 50 ;");
                    nodeLine.add(node);
                    drawLine();

                });
                customPane.getChildren().add(node);
                waitingForPlacement = false;
            }
        });
    }

    private void drawLine() {
        if(nodeLine.size() != 2) return;
        Node node1 = nodeLine.pop();
        Node node2 = nodeLine.pop();
        Line line = new Line(node1.getLayoutX() + 10 , node1.getLayoutY()+ 10 , node2.getLayoutX()+ 10 , node2.getLayoutY()+ 10);

        node1.setStyle("-fx-border-color: #d0d0d0 ; -fx-border-radius: 50 ; -fx-background-radius: 50 ;");
        node2.setStyle("-fx-border-color: #d0d0d0 ; -fx-border-radius: 50 ; -fx-background-radius: 50 ;");
        customPane.getChildren().add(line);
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
