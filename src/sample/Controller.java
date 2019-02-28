package sample;

import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class Controller {

    public Button btnNewNode;
    public Button btnNewLine;
    public Button btnFinish;
    public Button btnBfs;
    public Button btnDfs;
    public Pane customPane;
    public Label label1;
    private boolean waitingForPlacement = false;
    public int index =0;

    public void btnNewNodeClicked() {
        waitingForPlacement = true;

        double[] argument = setPaneListener();
        Node node = new Node(index++ , argument[0] , argument[1]);

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

    public double[] setPaneListener() {
        double[] tmp = new double[2];
        customPane.setOnMouseClicked(event -> {
        double centerX = 0,centerY =0;
            if (waitingForPlacement) {
                centerX = event.getX();
                centerY = event.getY();
//                createCiecle(centerX , centerY , value);
                waitingForPlacement = false;
                tmp[0] = centerX;
                tmp[1] = centerY;
            }

        });
        return tmp;
    }

    // drawing Ring :
//    public void createCiecle(double centerX , double centerY , int value){
//        // creating ring and add it to custom pane :
//        Circle circleBig = new Circle(centerX, centerY, 25);
//        Circle circleSmall = new Circle(centerX, centerY, 20);
//        circleBig.setFill(javafx.scene.paint.Color.BLUE);
//        circleSmall.setFill(javafx.scene.paint.Color.WHITE);
//
//        // add text to the circle :
//        Text text   = new Text(String.valueOf(value));
//        text.setX(centerX - 4);
//        text.setY(centerY + 3);
//
//        //add circle and its value to custom pane
//        customPane.getChildren().add(circleBig);
//        customPane.getChildren().add(circleSmall);
//        customPane.getChildren().add(text);
//    }
}
