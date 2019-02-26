package sample;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;

import java.util.LinkedList;
import java.util.Optional;

public class Controller {

    public Button btnNewNode;
    public Button btnNewLine;
    public Button btnFinish;
    public Button btnBfs;
    public Button btnDfs;
    public Pane customPane;
    public Label label1;
    private boolean waitingForPlacement = false;

    public void btnNewNodeClicked() {
        waitingForPlacement = true;
    }

    public void newLineclicked() {
        LinkedList< LinkedList<Integer> > graph = new LinkedList<>();
        LinkedList <Integer> Temp= new LinkedList<>();
        Temp.add(10);
        graph.get(1).add();
        graph.add(Temp);
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

    public void setPaneListener(int value) {
        customPane.setOnMouseClicked(event -> {
            if (waitingForPlacement) {

                double centerX = event.getX();
                double centerY = event.getY();

                createCiecle(centerX , centerY , value);
                waitingForPlacement = false;

            }
        });
    }

    // drawing Ring :
    public void createCiecle(double centerX , double centerY , int value){
        // creating ring and add it to custom pane :
        Circle circleBig = new Circle(centerX, centerY, 25);
        Circle circleSmall = new Circle(centerX, centerY, 20);
        circleBig.setFill(javafx.scene.paint.Color.BLUE);
        circleSmall.setFill(javafx.scene.paint.Color.WHITE);

        // add text to the circle :
        Text text   = new Text(String.valueOf(value));
        text.setX(centerX - 3);
        text.setY(centerY + 2);

        //add circle and its value to custom pane
        customPane.getChildren().add(circleBig);
        customPane.getChildren().add(circleSmall);
        customPane.getChildren().add(text);
    }
}
