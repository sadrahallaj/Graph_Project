package com.graphAlgorithm.view.main;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import com.graphAlgorithm.view.other.Node;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainPage {

    @FXML
    private Button btnNewNode;
    @FXML
    private Button btnFinish;
    @FXML
    private Button btnBfs;
    @FXML
    private Button btnDfs;
    @FXML
    private Pane customPane;
    @FXML
    private Button restart;
    @FXML
    private Button help;

    private boolean waitingForPlacement = false;
    private int index = 0;
    private boolean finished = false;
    private LinkedList<Node> nodeLine = new LinkedList<>();
    private LinkedList<Double> xDir = new LinkedList<>();
    private LinkedList<Double> yDir = new LinkedList<>();
    private LinkedList<LinkedList<Node>> nodesList = new LinkedList<>();


    @FXML
    private void btnNewNodeClicked() {

        btnNewNode.setOnMouseClicked(e -> btnNewNode.setVisible(false));
        waitingForPlacement = true;
        customPane.setOnMouseClicked(event -> {
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
                            System.out.println(e);
                        }
                    }
                });

                customPane.getChildren().add(node);
            }
        });
    }

    @FXML
    private void btnRestartClicked() {
        waitingForPlacement = false;
        btnNewNode.setVisible(true);
        btnFinish.setVisible(true);
        btnDfs.setVisible(false);
        btnBfs.setVisible(false);
        nodesList.clear();
        nodeLine.clear();
        xDir.clear();
        yDir.clear();
        index = 0;
        finished = false;
        btnFinish.setVisible(false);
        customPane.getChildren().clear();
    }

    @FXML
    private void Finishclicked() {
        if (!nodesList.isEmpty()) {
            waitingForPlacement = false;
            btnNewNode.setVisible(false);
            btnFinish.setVisible(false);
            btnDfs.setVisible(true);
            btnBfs.setVisible(true);
            finished = true;
        }

    }

    @FXML
    private void helpClicked() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("Information");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResourceAsStream("/source/inf.png"));
        stage.getIcons().add(image);
        alert.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/source/help.png"))));
        alert.setContentText("Create new vertex: In order to create a new vertex click on start button and for more vertices just click on screen.\n" +
                "\n" +
                "Create new connection: If you want to create a new connection between two vertices , first click on the desired vertex and then select the second.\n" +
                "\n" +
                "When your graph is completed, click on the finish button and then choose the type of search (dfs or bfs).\n" +
                "\n" +
                "Additionally, you can restart the process any time you want by clicking on the restart button.");
        alert.showAndWait();
    }

    @FXML
    private void bfsclicked() {

        for (int i = 0; i <nodesList.size() ; i++) {
            nodesList.get(i).get(0).setStyle("-fx-background-color: #cfcfcf; -fx-font-size: 16; -fx-background-radius: 50 ; -fx-pref-height: 50 ; -fx-pref-width: 50");
        }
        LinkedList<String> options = new LinkedList<>();
        options.add("random vertex");
        for (int i = 0; i < nodesList.size(); i++) {
            options.add(String.valueOf(nodesList.get(i).get(0).getIndex()));
        }
        ChoiceDialog d = new ChoiceDialog(options.get(0), options);
        d.setTitle("options");
        d.setHeaderText("Getting start vertex");
        d.setContentText("please select to start from which vertex : ");
        d.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/source/choice.png"))));
        d.setX(customPane.getWidth() / 2 + 320);
        d.setY(customPane.getHeight() / 2 - 50);
        Stage stage = (Stage) d.getDialogPane().getScene().getWindow();
        javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResourceAsStream("/source/options.png"));
        stage.getIcons().add(image);

        if (!d.showAndWait().isPresent()) return;
        else if (d.getSelectedItem() == "random vertex") {
            Random rand = new Random();
            BFS(rand.nextInt(nodesList.size()));
        } else {
            BFS(Integer.parseInt(d.getSelectedItem().toString()));
        }
    }

    @FXML
    private void dfsclicked() {

        for (int i = 0; i <nodesList.size() ; i++) {
            nodesList.get(i).get(0).setStyle("-fx-background-color: #cfcfcf; -fx-font-size: 16; -fx-background-radius: 50 ; -fx-pref-height: 50 ; -fx-pref-width: 50");
        }
        LinkedList<String> options = new LinkedList<>();
        options.add("random vertex");
        for (int i = 0; i < nodesList.size(); i++) {
            options.add(String.valueOf(nodesList.get(i).get(0).getIndex()));
        }
        ChoiceDialog d = new ChoiceDialog(options.get(0), options);
        d.setTitle("options");
        d.setHeaderText("Getting start vertex");
        d.setContentText("please select to start from which vertex : ");
        d.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/source/choice.png"))));
        d.setX(customPane.getWidth() / 2 + 320);
        d.setY(customPane.getHeight() / 2 - 50 );
        Stage stage = (Stage) d.getDialogPane().getScene().getWindow();
        javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResourceAsStream("/source/options.png"));
        stage.getIcons().add(image);

        if (!d.showAndWait().isPresent()) return;
        else if (d.getSelectedItem() == "random vertex") {
            Random rand = new Random();
            DFS(rand.nextInt(nodesList.size()));
        } else {
            DFS(Integer.parseInt(d.getSelectedItem().toString()));
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

    private void BFS(int s) {

        Thread thread;

        int[] finalS = new int[1];
        finalS[0] = s;

        thread = new Thread(() -> {
            btnDfs.setVisible(false);

            boolean visited[] = new boolean[nodesList.size()];

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
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            btnDfs.setVisible(true);
        });
        thread.start();
    }

    private void DFS(int v) {
        boolean visited[] = new boolean[nodesList.size()];
        Thread dfsThread;
        dfsThread = new Thread(() -> {
            btnBfs.setVisible(false);
            DFSUtil(nodesList.get(v).get(0).getIndex(), visited);
            btnBfs.setVisible(true);
        });

        dfsThread.start();
    }

    private void DFSUtil(int v, boolean visited[]) {
        visited[nodesList.get(v).get(0).getIndex()] = true;
        System.out.print(nodesList.get(v).get(0).getIndex() + " ");
        nodesList.get(v).get(0).setStyle("-fx-background-color: #f93f98 ;-fx-background-radius: 50 ;" +
                " -fx-text-fill: #e5e5e5 ; -fx-font-size: 16; -fx-pref-height: 50 ; -fx-pref-width: 50");

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Iterator<Node> i = nodesList.get(v).listIterator();
        while (i.hasNext()) {
            Node n = i.next();
            if (!visited[n.getIndex()])
                DFSUtil(n.getIndex(), visited);
        }
    }

}

