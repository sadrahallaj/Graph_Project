package com.graphAlgorithm.view.main;

import com.jfoenix.controls.JFXSlider;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import com.graphAlgorithm.view.other.Node;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class MainPage {

    @FXML
    private JFXSlider slider;
    @FXML
    private VBox speedControl;
    @FXML
    private Button btnFinish;
    @FXML
    private Button btnBfs;
    @FXML
    private Button btnDfs;
    @FXML
    private Pane customPane;
    @FXML
    private Button btnShortestPath;


    private boolean waitingForPlacement = false;
    private int index = 0;
    private ChoiceDialog choiceDialog;
    private boolean finished = false;
    private LinkedList<Node> nodeLine = new LinkedList<>();
    private LinkedList<Double> xDir = new LinkedList<>();
    private LinkedList<Double> yDir = new LinkedList<>();
    private LinkedList<LinkedList<Node>> nodesList = new LinkedList<>();

    private void setAlgoButVisible(boolean b){
        btnDfs.setVisible(b);
        btnBfs.setVisible(b);
        btnShortestPath.setVisible(b);
        speedControl.setVisible(b);
    }

    private void setAlgoButDisable(boolean b){
        btnDfs.setDisable(b);
        btnBfs.setDisable(b);
        btnShortestPath.setDisable(b);
    }

    @FXML
    void initialize(){
        //slider custom text
        slider.setValueFactory(arg0 -> Bindings.createStringBinding(() -> {
            DecimalFormat df = new DecimalFormat("#x");
            return df.format(slider.getValue());
        }, slider.valueProperty()));

        setAlgoButVisible(false);

        waitingForPlacement = true;
        customPane.setOnMouseClicked(event -> {
            btnFinish.setDisable(false);
            double centerX = event.getX() - 20;
            double centerY = event.getY() - 20;
            for (int i = 0; i < xDir.size(); i++) {
                double x = xDir.get(i);
                double y = yDir.get(i);
                if (centerX < x + 50 && centerX > x - 50 && centerY < y + 50 && centerY > y - 50) return;
            }
            if (event.getX() < 25 || event.getY() > customPane.getHeight() - 25 || event.getX() > customPane.getWidth() - 25 || event.getY() < 25)
                return;
            else if (waitingForPlacement) {
                btnFinish.setVisible(true);
                xDir.add(centerX);
                yDir.add(centerY);
                Node node = new Node(index++, centerX, centerY);
                LinkedList<Node> tmp = new LinkedList<>();
                tmp.add(node);
                nodesList.add(tmp);
                node.setOnMouseClicked(event1 -> {
                    if (!finished) {
                        try {
                            node.setStyle("-fx-background-color: #ff0000; -fx-font-size: 16; -fx-background-radius: 50 ; -fx-pref-height: 50 ; -fx-pref-width: 50");
                            nodeLine.add(node);
                            drawLine();
                        } catch (Exception e) {
                            //todo
                            System.out.println(e.toString());
                        }
                    }
                });
                customPane.getChildren().add(node);
            }
        });
    }

    @FXML
    private void restartHandler() {
        waitingForPlacement = false;
        btnFinish.setDisable(true);
        setAlgoButVisible(false);
        nodesList.clear();
        nodeLine.clear();
        xDir.clear();
        yDir.clear();
        index = 0;
        finished = false;
        customPane.getChildren().clear();
        initialize();
    }

    @FXML
    private void finishHandler() {
        if (!nodesList.isEmpty()) {
            waitingForPlacement = false;
            btnFinish.setDisable(true);
            setAlgoButVisible(true);
            finished = true;
        }
    }

    @FXML
    private void helpHandler() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("Information");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResourceAsStream("/source/inf.png"));
        stage.getIcons().add(image);
        alert.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/source/help.png"))));
        alert.setContentText("Create new vertex: In order to create first new vertex click on start button and for more vertices just click on screen.\n" +
                "\n" +
                "Create new connection: If you want to create first new connection between two vertices , first click on the desired vertex and then select the second.\n" +
                "\n" +
                "When your graph is completed, click on the finish button and then choose the type of search (dfs or bfs).\n" +
                "\n" +
                "Additionally, you can restart the process any time you want by clicking on the restart button.");
        alert.showAndWait();
    }

    private void stillDontKnow(){
        for (LinkedList<Node> nodes : nodesList) {
            nodes.get(0).setStyle("-fx-background-color: #cfcfcf; -fx-font-size: 16; -fx-background-radius: 50 ; -fx-pref-height: 50 ; -fx-pref-width: 50");
        }
        LinkedList<String> options = new LinkedList<>();
        options.add("random vertex");
        for (LinkedList<Node> nodes : nodesList) {
            options.add(String.valueOf(nodes.get(0).getIndex()));
        }
        choiceDialog = new ChoiceDialog(options.get(0), options);
        choiceDialog.setTitle("options");
        choiceDialog.setHeaderText("Getting start vertex");
        choiceDialog.setContentText("please select to start from which vertex : ");
        choiceDialog.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/source/choice.png"))));
        choiceDialog.setX(customPane.getWidth() / 2 + 320);
        choiceDialog.setY(customPane.getHeight() / 2 - 50);
        Stage stage = (Stage) choiceDialog.getDialogPane().getScene().getWindow();
        javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResourceAsStream("/source/options.png"));
        stage.getIcons().add(image);
    }

    @FXML
    private void BFS_Handler() {
        //todo
        stillDontKnow();
        if (!choiceDialog.showAndWait().isPresent()) return;
        else if (choiceDialog.getSelectedItem() == "random vertex") {
            Random rand = new Random();
            BFS_Algorithm(rand.nextInt(nodesList.size()));
        } else {
            BFS_Algorithm(Integer.parseInt(choiceDialog.getSelectedItem().toString()));
        }
    }

    @FXML
    private void DFS_Handler() {
        //todo
        stillDontKnow();
        if (!choiceDialog.showAndWait().isPresent()) return;
        else if (choiceDialog.getSelectedItem() == "random vertex") {
            Random rand = new Random();
            DFS_Algorithm(rand.nextInt(nodesList.size()));
        } else {
            DFS_Algorithm(Integer.parseInt(choiceDialog.getSelectedItem().toString()));
        }
    }

    private void drawLine() {
        if (nodeLine.size() != 2) return;
        Node node1 = nodeLine.pop();
        Node node2 = nodeLine.pop();
        Line line = new Line(node1.getLayoutX() + 20, node1.getLayoutY() + 20, node2.getLayoutX() + 20, node2.getLayoutY() + 20);
        line.setStrokeWidth(4);
        line.setSmooth(true);
        line.setStroke(Color.rgb(24, 17, 140));
        nodesList.get(node1.getIndex()).add(node2);
        nodesList.get(node2.getIndex()).add(node1);
        node1.setStyle("-fx-border-color: #d0d0d0 ;  -fx-font-size: 16; -fx-border-radius: 50 ; -fx-background-radius: 50 ; -fx-pref-height: 50 ; -fx-pref-width: 50");
        node2.setStyle("-fx-border-color: #d0d0d0 ;  -fx-font-size: 16; -fx-border-radius: 50 ; -fx-background-radius: 50 ;-fx-pref-height: 50 ; -fx-pref-width: 50");
        customPane.getChildren().add(line);
        line.toBack();
        node1.toFront();
        node2.toFront();
    }

    private void BFS_Algorithm(int s) {

        Thread thread;

        int[] finalS = new int[1];
        finalS[0] = s;

        thread = new Thread(() -> {
            setAlgoButDisable(true);

            boolean[] visited = new boolean[nodesList.size()];

            LinkedList<Node> queue = new LinkedList<>();

            visited[nodesList.get(finalS[0]).get(0).getIndex()] = true;
            queue.add(nodesList.get(finalS[0]).get(0));

            LinkedList<Integer> lineColor = new LinkedList<>();
            lineColor.add(finalS[0]);
            lineColor.add(finalS[0]);

            while (queue.size() != 0) {
                finalS[0] = queue.poll().getIndex();
                System.out.print(nodesList.get(finalS[0]).get(0).getIndex() + " ");
                nodesList.get(finalS[0]).get(0)
                        .setStyle("-fx-background-color:  #4d4bfa ; -fx-font-size: 16;-fx-background-radius: 50 ;" +
                                " -fx-text-fill: #fff ; -fx-pref-height: 50 ; -fx-pref-width: 50");

                Iterator<Node> i = nodesList.get(finalS[0]).listIterator();
                while (i.hasNext()) {
                    Node n = i.next();
                    if (!visited[n.getIndex()]) {
                        visited[n.getIndex()] = true;
                        queue.add(n);
                    }
                }
                // delay
                try {
                    Thread.sleep((long)(1000*(1/slider.getValue())));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            setAlgoButDisable(false);
        });
        thread.start();
    }

    private void DFS_Algorithm(int v) {
        boolean[] visited = new boolean[nodesList.size()];
        Thread dfsThread;
        dfsThread = new Thread(() -> {
            setAlgoButDisable(true);
            DFSUtil(nodesList.get(v).get(0).getIndex(), visited);
            setAlgoButDisable(false);
        });

        dfsThread.start();
    }

    private void DFSUtil(int v, boolean[] visited) {
        visited[nodesList.get(v).get(0).getIndex()] = true;
        System.out.print(nodesList.get(v).get(0).getIndex() + " ");
        nodesList.get(v).get(0).setStyle("-fx-background-color: #f93f98 ;-fx-background-radius: 50 ;" +
                " -fx-text-fill: #e5e5e5 ; -fx-font-size: 16; -fx-pref-height: 50 ; -fx-pref-width: 50");

        try {
            Thread.sleep((long)(1000*(1/slider.getValue())));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Node n : nodesList.get(v)) {
            if (!visited[n.getIndex()])
                DFSUtil(n.getIndex(), visited);
        }
    }

}

