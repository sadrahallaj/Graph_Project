package sample;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Controller {

    public Button btnNewNode;
    public Button btnFinish;
    public Button btnBfs;
    public Button btnDfs;
    public Pane customPane;
    public Button restart;
    public Button help;
    public boolean waitingForPlacement = false;
    public int index = 0;
    public boolean finished = false;
    public LinkedList<Node> nodeLine = new LinkedList<>();
    public LinkedList<Double> xDir = new LinkedList<>();
    public LinkedList<Double> yDir = new LinkedList<>();
    public LinkedList<LinkedList<Node>> nodesList = new LinkedList<>();


    public void btnNewNodeClicked() {

        btnNewNode.setOnMouseClicked(e -> btnNewNode.setVisible(false));
        waitingForPlacement = true;
        customPane.setOnMouseClicked(event -> {
            double centerX = event.getX() - 20;
            double centerY = event.getY() - 20;
            for (int i = 0; i < xDir.size() ; i++) {
                double x = xDir.get(i);
                double y = yDir.get(i);
                if(centerX < x + 20 &&  centerX > x - 20 && centerY < y + 20 && centerY > y - 20 ) return;
            }
            if (event.getX() < 25 || event.getY() > customPane.getHeight() - 25 || event.getX() > customPane.getWidth() - 25 || event.getY() < 25)
                return;
            else if (waitingForPlacement) {
                xDir.add(centerX);
                yDir.add(centerY);
                Node node = new Node(index++, centerX, centerY);
                LinkedList<Node> tmp = new LinkedList<>();
                tmp.add(node);
                nodesList.add(tmp);
                node.setOnMouseClicked(event1 -> {
                    if (!finished) {
                        try {
                            node.setStyle("-fx-background-color: #ff0000;-fx-background-radius: 50 ; -fx-pref-height: 40 ; -fx-pref-width: 40");
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

    private void drawLine() {
        if (nodeLine.size() != 2) return;
        Node node1 = nodeLine.pop();
        Node node2 = nodeLine.pop();
        Line line = new Line(node1.getLayoutX() + 20, node1.getLayoutY() + 20, node2.getLayoutX() + 20, node2.getLayoutY() + 20);
        line.setStrokeWidth(2);
        line.setSmooth(true);
        line.setStroke(Color.rgb(24, 17, 140));
        nodesList.get(node1.getIndex()).add(node2);
        nodesList.get(node2.getIndex()).add(node1);
        node1.setStyle("-fx-border-color: #d0d0d0 ; -fx-border-radius: 50 ; -fx-background-radius: 50 ; -fx-pref-height: 40 ; -fx-pref-width: 40");
        node2.setStyle("-fx-border-color: #d0d0d0 ; -fx-border-radius: 50 ; -fx-background-radius: 50 ;-fx-pref-height: 40 ; -fx-pref-width: 40");
        customPane.getChildren().add(line);
    }

    public void btnRestartClicked() {
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
        customPane.getChildren().clear();
    }

    public void Finishclicked() {
        waitingForPlacement = false;
        btnNewNode.setVisible(false);
        btnFinish.setVisible(false);
        btnDfs.setVisible(true);
        btnBfs.setVisible(true);
        finished = true;
    }

    public void helpClicked() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("Information");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResourceAsStream("inf.png"));
        stage.getIcons().add(image);
        alert.setContentText("Create new vertex: In order to create a new vertex click on new vertex button and for more vertices just click on screen.\n" +
                "\n" +
                "Create new connection: If you want to create a new connection between two vertices , first click on the desired vertex and then select the second.\n" +
                "\n" +
                "When your graph is completed, click on the finish button and then choose the type of search (dfs or bfs).\n" +
                "\n" +
                "Additionally, you can restart the process any time you want by clicking on the restart button.");
        alert.showAndWait();
    }


    public void bfsclicked() {

        LinkedList<String> options = new LinkedList<>();
        options.add("random vertex");
        for (int i = 0; i < nodesList.size(); i++) {
            options.add(String.valueOf(nodesList.get(i).get(0).getIndex()));
        }
        ChoiceDialog d = new ChoiceDialog(options.get(0), options);
        d.setHeaderText("Options");
        d.setContentText("please select to start from which vertex : ");
        if (!d.showAndWait().isPresent()) return;
        else if (d.getSelectedItem() == "default ( vertex with index 0 ) ") BFS(0);
        else if (d.getSelectedItem() == "random vertex") {
            Random rand = new Random();
            BFS(rand.nextInt(nodesList.size()));
        } else {
            BFS(Integer.parseInt(d.getSelectedItem().toString()));
        }

    }

    public void dfsclicked() {
        LinkedList<String> options = new LinkedList<>();
        options.add("random vertex");
        for (int i = 0; i < nodesList.size(); i++) {
            options.add(String.valueOf(nodesList.get(i).get(0).getIndex()));
        }
        ChoiceDialog d = new ChoiceDialog(options.get(0), options);
        d.setHeaderText("Options");
        d.setContentText("please select to start from which vertex : ");
        if (!d.showAndWait().isPresent()) return;
        else if (d.getSelectedItem() == "default ( vertex with index 0 ) ") DFS(0);
        else if (d.getSelectedItem() == "random vertex") {
            Random rand = new Random();
            DFS(rand.nextInt(nodesList.size()));
        } else {
            DFS(Integer.parseInt(d.getSelectedItem().toString()));
        }
    }


    public void BFS(int s) {

        Thread thread;

        int[] finalS = new int[1];
        finalS[0] = s;

        thread = new Thread(() -> {
            btnDfs.setVisible(false);

            boolean visited[] = new boolean[nodesList.size()];

            LinkedList<Node> queue = new LinkedList<>();

            visited[nodesList.get(finalS[0]).get(0).getIndex()] = true;
            queue.add(nodesList.get(finalS[0]).get(0));

            while (queue.size() != 0) {
                finalS[0] = queue.poll().getIndex();
                System.out.print(nodesList.get(finalS[0]).get(0).getIndex() + " ");
                nodesList.get(finalS[0]).get(0)
                        .setStyle("-fx-background-color: #4d4bfa ;-fx-background-radius: 50 ;" +
                                " -fx-text-fill: #fff ; -fx-pref-height: 40 ; -fx-pref-width: 40");

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

    void DFS(int v) {
        boolean visited[] = new boolean[nodesList.size()];
        Thread dfsThread;
        dfsThread = new Thread(() -> {
            btnBfs.setVisible(false);
            DFSUtil(nodesList.get(v).get(0).getIndex(), visited);
            btnBfs.setVisible(true);
        });

        dfsThread.start();
    }

    void DFSUtil(int v, boolean visited[]) {
        visited[nodesList.get(v).get(0).getIndex()] = true;
        System.out.print(nodesList.get(v).get(0).getIndex() + " ");
        nodesList.get(v).get(0).setStyle("-fx-background-color: #f93f98 ;-fx-background-radius: 50 ;" +
                " -fx-text-fill: #e5e5e5 ; -fx-pref-height: 40 ; -fx-pref-width: 40");

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

