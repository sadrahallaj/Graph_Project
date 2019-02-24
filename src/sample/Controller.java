package sample;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

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
        setMouseListener();
    }

    public void newLineclicked() {

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

    public void setMouseListener() {
        customPane.setOnMouseClicked(event -> {

//            Button button = new Button();
//            button.setLayoutX(event.getX());
//            button.setLayoutY(event.getY());
//            button.setText("Hi");
//
//            customPane.getChildren().add(button);
        });
    }
}
