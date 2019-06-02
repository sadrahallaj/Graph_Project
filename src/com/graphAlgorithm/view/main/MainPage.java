package com.graphAlgorithm.view.main;

import com.graphAlgorithm.model.DijkstraAlgorithm;
import com.graphAlgorithm.model.FileIO;
import com.graphAlgorithm.model.TspAlgorithmDP;
import com.graphAlgorithm.model.GraphDataSave;
import com.graphAlgorithm.view.componenets.Arrow;
import com.graphAlgorithm.view.componenets.Dialog;
import com.graphAlgorithm.view.componenets.ZoomableScrollPane;
import com.graphAlgorithm.view.componenets.GraphNode;
import com.graphAlgorithm.view.other.*;
import com.jfoenix.controls.JFXSlider;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

import static com.graphAlgorithm.view.other.MouseClickNotDragDetector.clickNotDragDetectingOn;
import static java.lang.Math.*;

public class MainPage {

    @FXML
    private JFXSlider slider;
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
    @FXML
    private BorderPane borderPane;

    private String NODE_STYLE_PURPLE = "-fx-background-color: #f93f98 ;-fx-background-radius: 50 ;" +
            " -fx-text-fill: #e5e5e5 ; -fx-font-size: 16; -fx-pref-height: 50 ; -fx-pref-width: 50";
    private String NODE_STYLE_BLUE = "-fx-background-color: #0000ff ;-fx-background-radius: 50 ;" +
            " -fx-text-fill: #e5e5e5 ; -fx-font-size: 16; -fx-pref-height: 50 ; -fx-pref-width: 50";
    private String NODE_STYLE_DEFULT = "-fx-background-color: #cfcfcf; -fx-font-size: 16;" +
            " -fx-background-radius: 50 ; -fx-pref-height: 50 ; -fx-pref-width: 50";

    private int indexOfGraph = 0;
    private boolean waitingForPlacement = false;
    private boolean isRunning = false, waitForLine = false;
    private Thread thread = new Thread();
    private Dialog dialog = new Dialog();
    private LinkedList<GraphNode> graphNodeLinesList = new LinkedList<>();
    private LinkedList<Double> xDirList = new LinkedList<>();
    private LinkedList<Double> yDirList = new LinkedList<>();
    private LinkedList<LinkedList<GraphNode>> nodesList = new LinkedList<>();
    private LinkedList<LinkedList<Pair<Integer, Integer>>> adjList = new LinkedList<>();
    private ZoomableScrollPane zoomableScrollPane;


    @FXML
    void initialize() {
        waitingForPlacement = true;
        zoomableScrollPane  = new ZoomableScrollPane(customPane);
        borderPane.setCenter(zoomableScrollPane);

        //slider custom text
        setSliderStyle();
        addNode();
    }

    @FXML
    private void BFSButtonHandler() {
        if(dfsBfsButtonHandler())
            BFS_Algorithm(dialog.getSelectedItem_Integer());
        setNodesDefaultColor();
    }

    @FXML
    private void DFSButtonHandler() {
        if (dfsBfsButtonHandler())
            DFS_Algorithm(dialog.getSelectedItem_Integer());
        setNodesDefaultColor();
    }

    @FXML
    private void tspAlgorithmHandler(){

        if (isThreadRunning()) {
            resetThread();
            return;
        }
//        // reset the colours of vertexes:
//        setNodesDefaultColor();

        final int sourceVertex;
        sourceVertex = tspAlgorithmGetSource();
        //stop if nothing selected
        if (sourceVertex == -1) return;

        TspAlgorithmDP tsp = new TspAlgorithmDP(
                sourceVertex , convertAdjListToMatrix(adjList));

        List<Integer> tspResultList;
        try { tspResultList = tsp.getTour(); }
        catch(Exception e){
            dialog.showInformationDialog(
                    "Can't be!","there is't a hamiltoni cycle!");
            return;
        }

        TSP_Algorithm(tspResultList);
        setNodesDefaultColor();
    }

    @FXML
    public void DIJButtonHandler() {
        if (isThreadRunning()) {
            resetThread();
            return;
        }
        isRunning = true;

//        // reset the colours of vertexes:
//        setNodesDefaultColor();

        final int sourceVertex, destinationVertex;

        //todo find a better way
        sourceVertex = DIJ_getSource();
        dialog.getChoiceDialogsOptions().remove(""+sourceVertex);
        destinationVertex = DIJ_getDestination();
        if(sourceVertex == -1  || destinationVertex == -1)return;
        //todo

        DIJ_Algorithm(sourceVertex, destinationVertex);
        isRunning = false;
        setNodesDefaultColor();
    }

    @FXML
    private void loadGraphButtonHandler(){

        File selectedFile = graphFileOpener();

        if (selectedFile == null) return;

        GraphDataSave graphData;
        try {
            graphData =  (GraphDataSave)FileIO.readAnObjectFromFile(selectedFile.getAbsolutePath());
            LoadDataToMainProgram(graphData);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException");
        }
    }

    @FXML
    private void saveAdjMatrix(){
        int[][] adjMatrix = new int[adjList.size()][adjList.size()];
        for (int i = 0; i<adjList.size();i++){
            for (int j = 0; j<adjList.get(i).size();j++) {
                adjMatrix[i][adjList.get(i).get(j).getFirst()] = adjList.get(i).get(j).getSecond();
            }
        }
    }

    @FXML
    private void restartButtonHandler() {
        waitingForPlacement = false;
        nodesList.clear();
        graphNodeLinesList.clear();
        xDirList.clear();
        yDirList.clear();
        indexOfGraph = 0;
        isRunning = false;
        customPane.getChildren().clear();
        initialize();
    }

    @FXML
    private void saveGraphButtonHandler(){
        File selectedFile = graphFileSaver();

        if(selectedFile == null) return;

        GraphDataSave saveData = new GraphDataSave(this.adjList, this.xDirList, this.yDirList, this.nodesList, this.indexOfGraph);
        try {
            FileIO.writeAnObjectToFile(selectedFile.getAbsolutePath(),saveData);
            dialog.showInformationDialog("Save File", "Graph saved.");
        } catch (IOException e) {
            //todo
            e.printStackTrace();
        }

    }

    private void setGraphNodeListener(GraphNode graphNode){
        graphNode.setOnMouseClicked(event1 -> {
            if (!isRunning) {
                try {
                    waitForLine = true;
                    graphNode.setStyle("-fx-background-color: #ff0000; -fx-font-size: 16; -fx-background-radius: 50 ; -fx-pref-height: 50 ; -fx-pref-width: 50");
                    graphNodeLinesList.add(graphNode);
                    drawLine();
                } catch (Exception e) {
                    //todo
                    System.out.println(e.toString());
                    graphNodeLinesList.clear();
                    waitForLine = false;
                    setNodesDefaultColor();
                }
            }
        });
    }

    private void drawLine() {
        if (graphNodeLinesList.size() != 2) return;
        int result = dialog.NumberInputDialogShow("Enter the edge weight:");

        GraphNode graphNode1 = graphNodeLinesList.pop();
        GraphNode graphNode2 = graphNodeLinesList.pop();

        _drawLine(graphNode1, graphNode2, result);
        setNodesDefaultColor();
    }

    private void _drawLine(GraphNode graphNode1, GraphNode graphNode2, int weight){

        Arrow arrow;

        double node1X = graphNode1.getLayoutX()+25, node1Y = graphNode1.getLayoutY()+25;
        double node2X = graphNode2.getLayoutX()+25,node2Y= graphNode2.getLayoutY()+25;

        Pair<Integer, Integer> temp = new Pair<>(graphNode2.getIndex(), weight);
        adjList.get(graphNode1.getIndex()).add(temp);

        Label label = makeLabel(weight+"", graphNode1, graphNode2, node1X, node1Y, node2X, node2Y);
        final double ALPHA = calculateAlpha(graphNode1, graphNode2);
        arrow = makeArrow(ALPHA, node1X,node1Y, node2X, node2Y);


        nodesList.get(graphNode1.getIndex()).add(graphNode2);

        graphNode1.setStyle(NODE_STYLE_PURPLE);
        graphNode2.setStyle(NODE_STYLE_PURPLE);

        customPane.getChildren().add(arrow);
        customPane.getChildren().add(label);

        arrow.toFront();
        graphNode1.toFront();
        graphNode2.toFront();
    }

    private Label makeLabel(String weight, GraphNode graphNode1, GraphNode graphNode2,
                            double node1X, double node1Y, double node2X, double node2Y ){
        Label label = new Label(weight);
        if(node1Y >= node2Y){
            label.setLayoutX(((graphNode1.getDirX()+25 + graphNode2.getDirX()+25)/2)  - (abs(node1Y - node2Y)/18) );
            label.setLayoutY(((graphNode1.getDirY()+25 + graphNode2.getDirY()+25)/2)  - (abs(node1X - node2X)/18) -5 );
        }else if (node1Y < node2Y){
            label.setLayoutX(((graphNode1.getDirX()+25 + graphNode2.getDirX()+25)/2)    + (abs(node1Y - node2Y)/18) );
            label.setLayoutY(((graphNode1.getDirY()+25 + graphNode2.getDirY()+25)/2)    + (abs(node1X - node2X)/18) -2 );
        }
        return label;
    }

    private Arrow makeArrow
            (double ALPHA, double node1X, double node1Y, double node2X, double node2Y){
        //set x and y for arrow
        double desX =(node2X ) + cos(ALPHA)*25;
        double desY =(node2Y) + sin(ALPHA)*25;

        double desX2 =(node2X) + cos(ALPHA)*25;
        double desY2 =(node2Y) - sin(ALPHA)*25;

        double desX3 =(node2X) - cos(ALPHA)*25;
        double desY3 =(node2Y) - sin(ALPHA)*25;

        double desX4 =(node2X) - cos(ALPHA)*25;
        double desY4 =(node2Y) + sin(ALPHA)*25;

        Arrow arrow = null;
        //draw line base on position
        if      (node1Y >= node2Y && node1X >= node2X)
            arrow = new Arrow(node1X, node1Y, desX , desY  , Arrow.defaultArrowHeadSize);
        else if (node1Y <= node2Y  && node1X >=node2X)
            arrow = new Arrow(node1X, node1Y, desX2, desY2 , Arrow.defaultArrowHeadSize);
        else if (node1Y <= node2Y  && node1X <= node2X)
            arrow = new Arrow(node1X, node1Y, desX3, desY3 , Arrow.defaultArrowHeadSize);
        else if (node1Y >= node2Y  && node1X <= node2X)
            arrow = new Arrow(node1X, node1Y, desX4, desY4 , Arrow.defaultArrowHeadSize);
        return arrow;
    }

    private double calculateAlpha(GraphNode graphNode1, GraphNode graphNode2){
        double ALPHA = Math.atan( abs(graphNode1.getDirX() - graphNode2.getDirX())  /
                abs(graphNode1.getDirY() - graphNode2.getDirY()) );
        if(Math.toDegrees(ALPHA) > 45) ALPHA = Math.toRadians(90 - Math.toDegrees(ALPHA));
        else if(Math.toDegrees(ALPHA) < 45) ALPHA = Math.toRadians(90 - Math.toDegrees(ALPHA));
        return ALPHA;
    }

    private FileChooser graphFileChooser(){
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("graph files (*.gr)", "*.gr");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("graph");

        //Show save file dialog
        return fileChooser;
    }

    private void TSP_Algorithm(List<Integer> tspResultList){
        thread = new Thread(() -> {
            isRunning = true;
            setAlgorithmButtonsDisable(true);
            btnTSP.setDisable(false);
            // coloring the nodes :
            for (Integer integer : tspResultList) {
                nodesList.get(integer).get(0).setStyle(NODE_STYLE_BLUE);
                MakeDelay();
            }
            setAlgorithmButtonsDisable(false);
            isRunning = false;
        });
        thread.start();
    }

    private File graphFileOpener(){
        return graphFileChooser().showOpenDialog(new Stage());
    }

    private File graphFileSaver(){
        return graphFileChooser().showSaveDialog(new Stage());
    }

    private void LoadDataToMainProgram(GraphDataSave graphData){
        restartButtonHandler();
        this.adjList = graphData.getAdjList();
        this.xDirList = graphData.getxDir();
        this.yDirList = graphData.getyDir();
        this.nodesList = graphData.getNodesList();
        this.indexOfGraph = graphData.getIndex();

        // load vertexes :
        for (int i = 0; i < xDirList.size(); i++) {
            GraphNode graphNode = new GraphNode(i, xDirList.get(i), yDirList.get(i));
            setGraphNodeListener(graphNode);
            customPane.getChildren().add(graphNode);
        }

        // load lines :
        for (int i = 0; i < adjList.size(); i++) {
            for (int j = 0; j < adjList.get(i).size(); j++) {
                Arrow arrow;

                GraphNode graphNode1 = nodesList.get(i).getFirst();
                GraphNode graphNode2 = nodesList.get(adjList.get(i).get(j).first).get(0);

                double node1X = graphNode1.getDirX()+25, node1Y = graphNode1.getDirY()+25;
                double node2X = graphNode2.getDirX()+25,node2Y= graphNode2.getDirY()+25;

                Label label =
                        makeLabel((adjList.get(i).get(j).second)+"", graphNode1, graphNode2, node1X, node1Y, node2X, node2Y);
                double ALPHA = calculateAlpha(graphNode1, graphNode2);
                arrow = makeArrow(ALPHA, node1X ,node1Y, node2X, node2Y);

                graphNode1.setStyle(NODE_STYLE_BLUE);
                graphNode2.setStyle(NODE_STYLE_BLUE);
                customPane.getChildren().add(arrow);
                customPane.getChildren().add(label);
                arrow.toBack();
                graphNode1.toFront();
                graphNode2.toFront();
            }
        }
    }

    private void setSliderStyle(){
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
    }

    private void addNode(){
        clickNotDragDetectingOn(customPane)
                .withPressedDurationTreshold(2500)
                .setOnMouseClickedNotDragged((mouseEvent) -> {
                    double centerX = mouseEvent.getX() - 20;
                    double centerY = mouseEvent.getY() - 20;
                    for (int i = 0; i < xDirList.size(); i++) {
                        double x = xDirList.get(i);
                        double y = yDirList.get(i);
                        if (centerX < x + 50 && centerX > x - 50 && centerY < y + 50 && centerY > y - 50) return;
                    }
                    if (mouseEvent.getX() < 25 || mouseEvent.getY() > customPane.getHeight() - 25 || mouseEvent.getX() > customPane.getWidth() - 25 || mouseEvent.getY() < 25)
                        return;
                    else if(waitForLine){
                        waitForLine = false;
                        graphNodeLinesList.clear();
                        setNodesDefaultColor();
                    }
                    else if (waitingForPlacement && !isRunning) {
                        xDirList.add(centerX);
                        yDirList.add(centerY);
                        GraphNode graphNode = new GraphNode(indexOfGraph++, centerX, centerY);
                        LinkedList<GraphNode> tmp = new LinkedList<>();
                        LinkedList<Pair<Integer, Integer>> tmp_2 = new LinkedList<>();
                        adjList.add(tmp_2);
                        tmp.add(graphNode);
                        nodesList.add(tmp);
                        setGraphNodeListener(graphNode);
                        customPane.getChildren().add(graphNode);
                    }
                });
    }

    private boolean isThreadRunning(){
        return thread.getState() != Thread.State.TERMINATED && thread.getState() != Thread.State.NEW;
    }

    private void resetThread(){
        thread.stop();
        setNodesDefaultColor();
        setAlgorithmButtonsDisable(false);
    }

    private void setNodesDefaultColor(){
        for (LinkedList<GraphNode> graphNodes : nodesList) {
            for (GraphNode graphNode : graphNodes) {
                graphNode.setStyle(NODE_STYLE_DEFULT);
            }
        }
    }

    private void DIJ_Algorithm(int sourceVertex, int destinationVertex){
        thread = new Thread(() -> {
            isRunning = true;
            setAlgorithmButtonsDisable(true);
            btnDJT.setDisable(false);

            DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm();
            dijkstraAlgorithm.algorithm(adjList, sourceVertex);
            LinkedList<Integer> path = dijkstraAlgorithm.shortestPath(destinationVertex);

            for (Integer integer : path) {
                nodesList.get(integer).get(0).setStyle(NODE_STYLE_BLUE);
                MakeDelay();
            }
            setAlgorithmButtonsDisable(false);
            isRunning = false;
        });
        thread.start();
    }

    private void BFS_Algorithm(int s) {
        int[] finalS = new int[1];
        finalS[0] = s;

        thread = new Thread(() -> {
            isRunning = true;
            setAlgorithmButtonsDisable(true);
            btnBfs.setDisable(false);

            boolean[] visited = new boolean[nodesList.size()];

            LinkedList<GraphNode> queue = new LinkedList<>();

            visited[nodesList.get(finalS[0]).get(0).getIndex()] = true;
            queue.add(nodesList.get(finalS[0]).get(0));

            LinkedList<Integer> lineColor = new LinkedList<>();
            lineColor.add(finalS[0]);
            lineColor.add(finalS[0]);

            while (queue.size() != 0) {
                finalS[0] = queue.poll().getIndex();
                System.out.print(nodesList.get(finalS[0]).get(0).getIndex() + " ");
                nodesList.get(finalS[0]).get(0)
                        .setStyle(NODE_STYLE_BLUE);

                for (GraphNode n : nodesList.get(finalS[0])) {
                    if (!visited[n.getIndex()]) {
                        visited[n.getIndex()] = true;
                        queue.add(n);
                    }
                }
                // MakeDelay
                MakeDelay();
            }

            setAlgorithmButtonsDisable(false);
            isRunning =false;
        });
        thread.start();
    }

    private void DFS_Algorithm(int v) {

        boolean[] isVisited = new boolean[nodesList.size()];

        thread = new Thread(() -> {
            isRunning = true;
            setAlgorithmButtonsDisable(true);
            btnDfs.setDisable(false);

            DFSUtil(nodesList.get(v).get(0).getIndex(), isVisited);

            setAlgorithmButtonsDisable(false);
            isRunning = false;
        });
        thread.start();
    }

    private void DFSUtil(int v, boolean[] visited) {
        visited[nodesList.get(v).get(0).getIndex()] = true;
        System.out.print(nodesList.get(v).get(0).getIndex() + " ");
        nodesList.get(v).get(0).setStyle(NODE_STYLE_BLUE);

        //MakeDelay
        MakeDelay();

        for (GraphNode n : nodesList.get(v)) {
            if (!visited[n.getIndex()])
                DFSUtil(n.getIndex(), visited);
        }
    }

    private void setAlgorithmButtonsDisable(boolean f){
        btnDfs.setDisable(f);
        btnBfs.setDisable(f);
        btnDJT.setDisable(f);
        btnTSP.setDisable(f);
    }

    private boolean dfsBfsButtonHandler(){
        if (isThreadRunning()) {
            resetThread();
            return false;
        }
        dialog.setChoiceOptionWithRandomVertesec(nodesList);
        dialog.makeChoiceDialog("Getting source vertex",
                "please select the source vertex : ","");

        return dialog.getChoiceDialog().showAndWait().isPresent();
    }

    private int DIJ_getSource(){
        dialog.setChoiceOption(nodesList);
        dialog.makeChoiceDialog("options","Getting source vertex","please select the source vertex : ");
        if (!dialog.getChoiceDialog().showAndWait().isPresent()) return -1;
        else return dialog.getSelectedItem_Integer();
    }

    private int DIJ_getDestination(){
        dialog.makeChoiceDialog("options","Getting destination vertex","please select the destination vertex : ");
        if (!dialog.getChoiceDialog().showAndWait().isPresent()) return -1;
        else return dialog.getSelectedItem_Integer();
    }

    private int tspAlgorithmGetSource(){
        dialog.setChoiceOption(nodesList);
        dialog.makeChoiceDialog("select","select source","select source");
        if (!dialog.getChoiceDialog().showAndWait().isPresent()) return -1;
        return  dialog.getSelectedItem_Integer();
    }

    private double[][] convertAdjListToMatrix(
            LinkedList<LinkedList<Pair<Integer, Integer>>> adjList){
        double[][] dataMatrix  = new double[adjList.size()][adjList.size()];
        //initialise
        for (int i=0; i<adjList.size(); i++){
            for (int j = 0; j < adjList.size(); j++) {
                dataMatrix[i][j] = Double.MAX_VALUE ;
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

    private void MakeDelay(){
        try {
            Thread.sleep((long) (1000 * (1 / slider.getValue())));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}