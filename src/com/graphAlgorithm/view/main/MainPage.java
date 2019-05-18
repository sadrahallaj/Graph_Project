package com.graphAlgorithm.view.main;

import com.graphAlgorithm.model.DijkstraAlgorithm;
import com.graphAlgorithm.view.other.Node;
import com.graphAlgorithm.view.other.Pair;
import com.jfoenix.controls.JFXSlider;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Random;

import static java.lang.Math.*;


//todo
//خودش را نباید بتواند انتخاب کند
// اصلاح نحوه ی نمایش خط ها
// نمیتواند سورس و دیستنیشن را یکی انتخاب کند


public class MainPage {

    @FXML
    private JFXSlider slider;
    @FXML
    private Button btnFinish;
    @FXML
    private Button btnBfs;
    @FXML
    private Button btnDfs;
    @FXML
    private Pane customPane;

    private boolean waitingForPlacement = false;
    private int index = 0;
    private ChoiceDialog choiceDialog;
    private ChoiceDialog choiceDialogSource;
    private ChoiceDialog choiceDialogVertex;
    private boolean finished = false;
    private LinkedList<Node> nodeLine = new LinkedList<>();
    private LinkedList<Double> xDir = new LinkedList<>();
    private LinkedList<Double> yDir = new LinkedList<>();
    private LinkedList<LinkedList<Node>> nodesList = new LinkedList<>();
    private LinkedList<LinkedList<Pair<Integer, Integer>>> adjList = new LinkedList<>();

    @FXML
    void initialize() {
        //slider custom text
        slider.setValueFactory(new Callback<JFXSlider, StringBinding>() {
            @Override
            public StringBinding call(JFXSlider arg0) {
                return Bindings.createStringBinding(new java.util.concurrent.Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        DecimalFormat df = new DecimalFormat("#x");
                        return df.format(slider.getValue());
                    }
                }, slider.valueProperty());
            }
        });


        btnDfs.setVisible(false);
        btnBfs.setVisible(false);

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
                LinkedList<Pair<Integer, Integer>> tmp_2 = new LinkedList<>();
                adjList.add(tmp_2);
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
        btnDfs.setVisible(false);
        btnBfs.setVisible(false);
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
            btnDfs.setVisible(true);
            btnBfs.setVisible(true);
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

    @FXML
    private void BFS_Handler() {

        //set Choice Dialog >>
        LinkedList<String> options = new LinkedList<>();
        options.add("random vertex");
        for (LinkedList<Node> nodes : nodesList) {
            options.add(String.valueOf(nodes.get(0).getIndex()));
        }
        setChoiceDialog("Getting source vertex",
                "please select the source vertex : ",options);
        // << set Choice Dialog

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

        //set Choice Dialog >>
        LinkedList<String> options = new LinkedList<>();
        options.add("random vertex");
        for (LinkedList<Node> nodes : nodesList) {
            options.add(String.valueOf(nodes.get(0).getIndex()));
        }
        setChoiceDialog("Getting source vertex",
                "please select the source vertex : ",options);
        // << set Choice Dialog

        if (!choiceDialog.showAndWait().isPresent()) return;
        else if (choiceDialog.getSelectedItem() == "random vertex") {
            Random rand = new Random();
            DFS_Algorithm(rand.nextInt(nodesList.size()));
        } else {
            DFS_Algorithm(Integer.parseInt(choiceDialog.getSelectedItem().toString()));
        }
    }

    @FXML
    public void DIJ_Handler() {
        // reset the colours of vertexes :
        for (LinkedList<Node> nodes : nodesList) {
            nodes.get(0).setStyle("-fx-background-color: #cfcfcf; -fx-font-size: 16;" +
                    " -fx-background-radius: 50 ; -fx-pref-height: 50 ; -fx-pref-width: 50");
        }

        int sourceVertex, destinationVertex;

        // getting the source vertex :
        LinkedList<String> options = new LinkedList<>();
        for (LinkedList<Node> nodes : nodesList) {
            options.add(String.valueOf(nodes.get(0).getIndex()));
        }
        choiceDialogSource = new ChoiceDialog(options.get(0), options);
        choiceDialogSource.setTitle("options");
        choiceDialogSource.setHeaderText("Getting source vertex");
        choiceDialogSource.setContentText("please select the source vertex : ");
        choiceDialogSource.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/source/choice.png"))));
        choiceDialogSource.setX(customPane.getWidth() / 2 + 320);
        choiceDialogSource.setY(customPane.getHeight() / 2 - 50);
        Stage stage = (Stage) choiceDialogSource.getDialogPane().getScene().getWindow();
        javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResourceAsStream("/source/options.png"));
        stage.getIcons().add(image);
        if (!choiceDialogSource.showAndWait().isPresent()) return;
        else sourceVertex = Integer.parseInt(choiceDialogSource.getSelectedItem().toString());

        // getting the destination vertex  :
        choiceDialogVertex = new ChoiceDialog(options.get(0), options);
        choiceDialogVertex.setTitle("options");
        choiceDialogVertex.setHeaderText("Getting destination Vertex");
        choiceDialogVertex.setContentText("please select the destination vertex : ");
        choiceDialogVertex.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/source/choice.png"))));
        choiceDialogVertex.setX(customPane.getWidth() / 2 + 320);
        choiceDialogVertex.setY(customPane.getHeight() / 2 - 50);
        Stage stage2 = (Stage) choiceDialogVertex.getDialogPane().getScene().getWindow();
        stage2.getIcons().add(image);
        if (!choiceDialogVertex.showAndWait().isPresent()) return;
        else destinationVertex = Integer.parseInt(choiceDialogVertex.getSelectedItem().toString());


        Thread dijkstraThread;
        dijkstraThread = new Thread(() -> {

            DijkstraAlgorithm dijkstrasAlgorithm = new DijkstraAlgorithm();
            dijkstrasAlgorithm.algorithm(adjList, sourceVertex);
            LinkedList<Integer> path = dijkstrasAlgorithm.shortestPath(destinationVertex);
            for (int i = 0; i < path.size(); i++) {
                System.out.println(path.get(i));
                nodesList.get(path.get(i)).get(0).setStyle("-fx-background-color: #f93f98 ;-fx-background-radius: 50 ;" +
                        " -fx-text-fill: #e5e5e5 ; -fx-font-size: 16; -fx-pref-height: 50 ; -fx-pref-width: 50");

                // delay
                try {
                    Thread.sleep((long) (1000 * (1 / slider.getValue())));
                    //                System.out.println((long)(1000*(1/slider.getValue())));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        dijkstraThread.start();
    }

    private void drawLine() {
        if (nodeLine.size() != 2) return;
        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle(" ");
        dialog.setContentText("Enter the edge weight:");
        Optional<String> result = dialog.showAndWait();

        Node node1 = nodeLine.pop();
        Node node2 = nodeLine.pop();

        double node1X = node1.getLayoutX()+25, node1Y =node1.getLayoutY()+25;
        double node2X =node2.getLayoutX()+25,node2Y=node2.getLayoutY()+25;

        Pair<Integer, Integer> temp = new Pair<>(node2.getIndex(), Integer.parseInt(result.get()));
        adjList.get(node1.getIndex()).add(temp);
        Label w = new Label(String.valueOf(Integer.parseInt(result.get())));

        if(node1Y > node2Y){
            w.setLayoutX((node1.getLayoutX() + 25 + node2.getLayoutX() + 25)/2 -5);
            w.setLayoutY((node1.getLayoutY() + 25 + node2.getLayoutY() + 25)/2 - 15);
        }else if (node1Y <= node2Y){
            w.setLayoutX((node1.getLayoutX() + 25 + node2.getLayoutX() + 25)/2 + 5);
            w.setLayoutY((node1.getLayoutY() + 25 + node2.getLayoutY() + 25)/2 + 15);
        }
//        Line line = new Line(node1.getLayoutX() + 20, node1.getLayoutY() + 20, node2.getLayoutX() + 20, node2.getLayoutY() + 20);
//        line.setStrokeWidth(4);
//        line.setSmooth(true);
//        line.setStroke(Color.rgb(24, 17, 140));

        double alfa = Math.atan( abs(node1.getLayoutX() - node2.getLayoutX())  /
                abs(node1.getLayoutY() - node2.getLayoutY()) );
        double alfa2 = alfa;
        if(Math.toDegrees(alfa) > 45) alfa = Math.toRadians(90 - Math.toDegrees(alfa));
        else if(Math.toDegrees(alfa) < 45) alfa = Math.toRadians(90 - Math.toDegrees(alfa));
        System.out.println(Math.toDegrees(alfa));



        double desX =(node2X ) + cos(alfa)*25;
        double desY =(node2Y) + sin(alfa)*25;

        double desX2 =(node2X) + cos(alfa)*25;
        double desY2 =(node2Y) - sin(alfa)*25;

        double desX3 =(node2X) - cos(alfa)*25;
        double desY3 =(node2Y) - sin(alfa)*25;

        double desX4 =(node2X) - cos(alfa)*25;
        double desY4 =(node2Y) + sin(alfa)*25;

        class Arrow extends Path {
            private static final double defaultArrowHeadSize = 12.0;

            public Arrow(double startX, double startY, double endX, double endY, double arrowHeadSize){
                super();
                strokeProperty().bind(fillProperty());
                setFill(Color.BLUE);

                //Line
                getElements().add(new MoveTo(startX , startY ));
                getElements().add(new LineTo(endX , endY ));

                //ArrowHead
                double angle = Math.atan2((endY - startY), (endX - startX)) - Math.PI / 2.0;
                double sin = Math.sin(angle);
                double cos = Math.cos(angle);
                //point1
                double x1 = (- 1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * arrowHeadSize + endX;
                double y1 = (- 1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * arrowHeadSize + endY;
                //point2
                double x2 = (1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * arrowHeadSize + endX;
                double y2 = (1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * arrowHeadSize + endY;

                getElements().add(new LineTo(x1, y1));
                getElements().add(new LineTo(x2, y2));
                getElements().add(new LineTo(endX, endY));
            }

            public Arrow(double startX, double startY, double endX, double endY){
                this(startX, startY, endX, endY, defaultArrowHeadSize);
            }
        }
        Arrow arrow = null;

        if (node1Y>node2Y && node1X>node2X) {
            arrow = new Arrow(node1X, node1Y, desX , desY  , Arrow.defaultArrowHeadSize);
        }
        else if (node1Y<node2Y  && node1X>node2X) {
            arrow = new Arrow(node1X, node1Y, desX2, desY2 , Arrow.defaultArrowHeadSize);
        }
        else if (node1Y<node2Y  && node1X<node2X) {
            arrow = new Arrow(node1X, node1Y, desX3, desY3 , Arrow.defaultArrowHeadSize);
        }
        else if (node1Y>node2Y  && node1X<node2X) {
            arrow = new Arrow(node1X, node1Y, desX4, desY4 , Arrow.defaultArrowHeadSize);
        }

        nodesList.get(node1.getIndex()).add(node2);
//        nodesList.get(node2.getIndex()).add(node1);

        node1.setStyle("-fx-border-color: #d0d0d0 ;  -fx-font-size: 16; -fx-border-radius: 50 ; -fx-background-radius: 50 ; -fx-pref-height: 50 ; -fx-pref-width: 50");
        node2.setStyle("-fx-border-color: #d0d0d0 ;  -fx-font-size: 16; -fx-border-radius: 50 ; -fx-background-radius: 50 ;-fx-pref-height: 50 ; -fx-pref-width: 50");
//        customPane.getChildren().add(line);
        customPane.getChildren().add(arrow);
        customPane.getChildren().add(w);
//        line.toBack();
        arrow.toFront();
        node1.toFront();
        node2.toFront();
    }

    private void setNodesDefaultColor(){
        for (LinkedList<Node> nodes : nodesList) {
            nodes.get(0).setStyle("-fx-background-color: #cfcfcf; -fx-font-size: 16; -fx-background-radius: 50 ; -fx-pref-height: 50 ; -fx-pref-width: 50");
        }
    }

    private void setChoiceDialog(String headerText, String contentText, LinkedList<String> options) {
        setNodesDefaultColor();
        choiceDialog = new ChoiceDialog(options.get(0), options);
        choiceDialog.setTitle("options");
        choiceDialog.setHeaderText(headerText);
        choiceDialog.setContentText(contentText);
        choiceDialog.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/source/choice.png"))));
        choiceDialog.setX(customPane.getWidth() / 2 + 320);
        choiceDialog.setY(customPane.getHeight() / 2 - 50);
        Stage stage = (Stage) choiceDialog.getDialogPane().getScene().getWindow();
        javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResourceAsStream("/source/options.png"));
        stage.getIcons().add(image);
    }

    private void BFS_Algorithm(int s) {

        Thread thread;

        int[] finalS = new int[1];
        finalS[0] = s;

        thread = new Thread(() -> {
            btnDfs.setVisible(false);

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
                    Thread.sleep((long) (1000 * (1 / slider.getValue())));
                    System.out.println((long) (1000 * (1 / slider.getValue())));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            btnDfs.setVisible(true);
        });
        thread.start();
    }

    private void DFS_Algorithm(int v) {
        boolean[] visited = new boolean[nodesList.size()];
        Thread dfsThread;
        dfsThread = new Thread(() -> {
            btnBfs.setVisible(false);
            DFSUtil(nodesList.get(v).get(0).getIndex(), visited);
            btnBfs.setVisible(true);
        });

        dfsThread.start();
    }

    private void DFSUtil(int v, boolean[] visited) {
        visited[nodesList.get(v).get(0).getIndex()] = true;
        System.out.print(nodesList.get(v).get(0).getIndex() + " ");
        nodesList.get(v).get(0).setStyle("-fx-background-color: #f93f98 ;-fx-background-radius: 50 ;" +
                " -fx-text-fill: #e5e5e5 ; -fx-font-size: 16; -fx-pref-height: 50 ; -fx-pref-width: 50");

        try {
            Thread.sleep((long) (1000 * (1 / slider.getValue())));
            System.out.println(1 / slider.getValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Node n : nodesList.get(v)) {
            if (!visited[n.getIndex()])
                DFSUtil(n.getIndex(), visited);
        }
    }


}
