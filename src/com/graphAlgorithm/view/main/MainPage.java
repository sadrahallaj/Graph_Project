package com.graphAlgorithm.view.main;

import com.graphAlgorithm.model.DijkstraAlgorithm;
import com.graphAlgorithm.model.TspDynamicProgrammingRecursive;
import com.graphAlgorithm.view.other.Arrow;
import com.graphAlgorithm.view.other.Node;
import com.graphAlgorithm.view.other.Pair;
import com.jfoenix.controls.JFXSlider;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.text.DecimalFormat;
import java.util.*;

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
    private Button btnDJT;
    @FXML
    private Button btnTSP;
    @FXML
    private Pane customPane;

    private Thread thread = new Thread();
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

    void setAlgoButtDisble(boolean f){
        btnDfs.setDisable(f);
        btnBfs.setDisable(f);
        btnDJT.setDisable(f);
        btnTSP.setDisable(f);
    }
    void setAlgoButtVsible(boolean f){
        btnDfs.setVisible(f);
        btnBfs.setVisible(f);
        btnDJT.setVisible(f);
        btnTSP.setVisible(f);

    }

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


        setAlgoButtDisble(true);

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
        setAlgoButtDisble(true);
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
            setAlgoButtDisble(false);
            finished = true;
        }
    }

    @FXML
    private void BFS_Handler() {
        if (isThreadRunning()) {
            resetThread();
            return;
        }

        dfs_bfsShowDialog();

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
        if (isThreadRunning()) {
            resetThread();
            return;
        }

        dfs_bfsShowDialog();

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
        if (isThreadRunning()) {
            resetThread();
            return;
        }

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


        thread = new Thread(() -> {
            setAlgoButtDisble(true);
            btnDJT.setDisable(false);

            DijkstraAlgorithm dijkstrasAlgorithm = new DijkstraAlgorithm();
            dijkstrasAlgorithm.algorithm(adjList, sourceVertex);
            LinkedList<Integer> path = dijkstrasAlgorithm.shortestPath(destinationVertex);
            for (int i = 0; i < path.size(); i++) {
                System.out.println(path.get(i));
                nodesList.get(path.get(i)).get(0).setStyle("-fx-background-color: #f93f98 ;-fx-background-radius: 50 ;" +
                        " -fx-text-fill: #e5e5e5 ; -fx-font-size: 16; -fx-pref-height: 50 ; -fx-pref-width: 50");

                // delay
                delay();
            }

            setAlgoButtDisble(false);
        });
        thread.start();
    }

    private void dfs_bfsShowDialog(){
        //set Choice Dialog >>
        LinkedList<String> options = new LinkedList<>();
        options.add("random vertex");
        for (LinkedList<Node> nodes : nodesList) {
            options.add(String.valueOf(nodes.get(0).getIndex()));
        }
        setChoiceDialog("Getting source vertex",
                "please select the source vertex : ",options);
        // << set Choice Dialog
    }

    private boolean isThreadRunning(){
        if (thread.getState() != Thread.State.TERMINATED && thread.getState() != Thread.State.NEW) {
            return true;
        }else return false;
    }

    private void resetThread(){
        thread.stop();
        setAlgoButtDisble(false);
        setNodesDefaultColor();
    }

    private void drawLine() {
        Arrow arrow = null;

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

        if(node1Y >= node2Y){
            //todo
            w.setLayoutX(((node1.getLayoutX()+25 + node2.getLayoutX()+25)/2)  - (abs(node1Y - node2Y)/18) );
            w.setLayoutY(((node1.getLayoutY()+25 + node2.getLayoutY()+25)/2)  - (abs(node1X - node2X)/18) -5 );
        }else if (node1Y < node2Y){
            //todo
            w.setLayoutX(((node1.getLayoutX()+25 + node2.getLayoutX()+25)/2)    + (abs(node1Y - node2Y)/18) );
            w.setLayoutY(((node1.getLayoutY()+25 + node2.getLayoutY()+25)/2)    + (abs(node1X - node2X)/18) -2 );
        }

        //find alfa degree
        double alfa = Math.atan( abs(node1.getLayoutX() - node2.getLayoutX())  /
                abs(node1.getLayoutY() - node2.getLayoutY()) );
        if(Math.toDegrees(alfa) > 45) alfa = Math.toRadians(90 - Math.toDegrees(alfa));
        else if(Math.toDegrees(alfa) < 45) alfa = Math.toRadians(90 - Math.toDegrees(alfa));
        System.out.println(Math.toDegrees(alfa));

        //set x and y for arrow
        double desX =(node2X ) + cos(alfa)*25;
        double desY =(node2Y) + sin(alfa)*25;

        double desX2 =(node2X) + cos(alfa)*25;
        double desY2 =(node2Y) - sin(alfa)*25;

        double desX3 =(node2X) - cos(alfa)*25;
        double desY3 =(node2Y) - sin(alfa)*25;

        double desX4 =(node2X) - cos(alfa)*25;
        double desY4 =(node2Y) + sin(alfa)*25;


        //draw line base on position
        if      (node1Y >= node2Y && node1X >= node2X)
            arrow = new Arrow(node1X, node1Y, desX , desY  , Arrow.defaultArrowHeadSize);
        else if (node1Y <= node2Y  && node1X >=node2X)
            arrow = new Arrow(node1X, node1Y, desX2, desY2 , Arrow.defaultArrowHeadSize);
        else if (node1Y <= node2Y  && node1X <= node2X)
            arrow = new Arrow(node1X, node1Y, desX3, desY3 , Arrow.defaultArrowHeadSize);
        else if (node1Y >= node2Y  && node1X <= node2X)
            arrow = new Arrow(node1X, node1Y, desX4, desY4 , Arrow.defaultArrowHeadSize);


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
        int[] finalS = new int[1];
        finalS[0] = s;

        thread = new Thread(() -> {
            setAlgoButtDisble(true);
            btnBfs.setDisable(false);

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
               delay();
            }

            setAlgoButtDisble(false);
        });

        thread.start();
    }

    private void DFS_Algorithm(int v) {

        boolean[] visited = new boolean[nodesList.size()];

        thread = new Thread(() -> {
            setAlgoButtDisble(true);
            btnDfs.setDisable(false);

            DFSUtil(nodesList.get(v).get(0).getIndex(), visited);

            setAlgoButtDisble(false);
        });

        thread.start();

    }

    private void DFSUtil(int v, boolean[] visited) {
        visited[nodesList.get(v).get(0).getIndex()] = true;
        System.out.print(nodesList.get(v).get(0).getIndex() + " ");
        nodesList.get(v).get(0).setStyle("-fx-background-color: #f93f98 ;-fx-background-radius: 50 ;" +
                " -fx-text-fill: #e5e5e5 ; -fx-font-size: 16; -fx-pref-height: 50 ; -fx-pref-width: 50");

        //delay
        delay();

        for (Node n : nodesList.get(v)) {
            if (!visited[n.getIndex()])
                DFSUtil(n.getIndex(), visited);
        }
    }


    // convert adjlist to distance matrix :
    public double[][] convertAdjListToMatrix(LinkedList<LinkedList<Pair<Integer,Integer>>> adjList){
        double[][] dataMatrix  = new double[adjList.size()][adjList.size()];

        //initialise
        for (int i=0; i<adjList.size(); i++){
            for (int j = 0; j < adjList.size(); j++) {
                dataMatrix[i][j] = 0 ;
            }
        }

        // filling the values of matrix with adjList :
        for (int i = 0; i < adjList.size() ; i++) {
            for (int j = 0; j < adjList.get(i).size(); j++) {
                Pair<Integer , Integer> tmp = adjList.get(i).get(j);
                dataMatrix[i][tmp.getFirst()] = tmp.getSecond();
            }
        }

        return dataMatrix ;
    }

    public void tsp_Dp_Handler(){
        int sourceVertex;
        if (isThreadRunning()) {
            resetThread();
            return;
        }

        // reset the colours of vertexes :
        setNodesDefaultColor();

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


        double [][] distanceMatrix = convertAdjListToMatrix(adjList);
        TspDynamicProgrammingRecursive tsp = new TspDynamicProgrammingRecursive(  sourceVertex , distanceMatrix);
        List<Integer> tspResultList =  tsp.getTour();

        thread = new Thread(() -> {
            setAlgoButtDisble(true);
            btnTSP.setDisable(false);

            // colouring the nodes :
            for (int i = 0; i < tspResultList.size(); i++) {
                System.out.println(tspResultList.get(i));
                nodesList.get(tspResultList.get(i)).get(0).setStyle("-fx-background-color: #f93f98 ;-fx-background-radius: 50 ;" +
                        " -fx-text-fill: #e5e5e5 ; -fx-font-size: 16; -fx-pref-height: 50 ; -fx-pref-width: 50");

                // delay
                delay();
            }

            setAlgoButtDisble(false);
        });
        thread.start();
    }

    public void delay(){
        try {
            Thread.sleep((long) (1000 * (1 / slider.getValue())));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
