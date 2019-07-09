package com.graphAlgorithm.view.main;

import com.graphAlgorithm.model.*;
import com.graphAlgorithm.view.componenets.*;
import com.graphAlgorithm.view.componenets.Dialog;
import com.graphAlgorithm.view.other.*;
import com.jfoenix.controls.JFXSlider;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
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

    private String NODE_STYLE_SELCTION = "-fx-background-color: #ff0b00; -fx-font-size: 16;" +
            " -fx-background-radius: 50 ; -fx-pref-height: 50 ; -fx-pref-width: 50; -fx-text-fill: black; -fx-font-weight: bold";

    private String NODE_STYLE_DEFULT = "-fx-background-color: #34495e; -fx-font-size: 16;" +
            " -fx-background-radius: 50 ; -fx-pref-height: 50 ; -fx-pref-width: 50; -fx-text-fill: white; -fx-font-weight: bold";

    private int[][] adjMatrix;
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
    private LinkedList<LinkedList<Pair<GraphNode,Line>>> allInNode
            = new LinkedList<>(), allOutNode = new LinkedList<>();
    private ZoomableScrollPane zoomableScrollPane;
    private boolean deleteMode = false;
    final Delta dragDelta = new Delta();
    private LinkedList<LinkedList<Pair<GraphNode,Line>>> allGraphState = new LinkedList<>();
    class Delta {
        double x, y;
        int index;
    }


    @FXML
    void initialize() {
        setNodesDefaultColor();
        waitingForPlacement = true;
        zoomableScrollPane  = new ZoomableScrollPane(customPane);
        borderPane.setCenter(zoomableScrollPane);

        //slider custom text
        setSliderStyle();
        addNodeListener();
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

        final int sourceVertex;
        sourceVertex = tspAlgorithmGetSource();
        //stop if nothing selected
        if (sourceVertex == -1) return;

        TspAlgorithmDP tsp = new TspAlgorithmDP(
                sourceVertex , convertAdjListToMatrix(adjList));

        List<Integer> tspResultList;
        try {
            tspResultList = tsp.getTour();
            System.out.println(tspResultList.toString());
        }
        catch(Exception e){
            dialog.showInformationDialog(
                    "Can't be!","there is't a hamiltonian cycle!");
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

        final int sourceVertex, destinationVertex;

        //todo find a better way
        sourceVertex = DIJ_getSource();
        if(sourceVertex == -1) return;
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
    private void saveGraphButtonHandler(){
        File selectedFile = graphFileSaver();

        if(selectedFile == null) return;

        GraphDataSave saveData = new GraphDataSave(this.adjList, this.xDirList, this.yDirList,
                this.nodesList, this.allGraphState, this.allInNode, this.allOutNode, this.indexOfGraph);

        try {
            FileIO.writeAnObjectToFile(selectedFile.getAbsolutePath(),saveData);
            dialog.showInformationDialog("Save File", "Graph saved.");
        } catch (IOException e) {
            //todo
            e.printStackTrace();
        }
    }

    @FXML
    private void saveAdjMatrix(){
        adjMatrix = new int[adjList.size()][adjList.size()];
        for (int i = 0; i<adjList.size();i++){
            for (int j = 0; j<adjList.get(i).size();j++){
                adjMatrix[i][adjList.get(i).get(j).getFirst()] = adjList.get(i).get(j).getSecond();
            }
        }
        String string = "";
        for (int i=0; i < adjMatrix.length; i++ ){
            for (int j=0; j < adjMatrix.length; j++ ){
                string = string + adjMatrix[i][j] + " ";
            }
            string = string + "\n";
        }
        System.out.println(string);
    }

    @FXML
    private void loadFromMatrix(){
        customPane.getChildren().clear();
        xDirList.clear();
        yDirList.clear();

        int x = (int)customPane.getPrefWidth()/2;
        int y = (int)customPane.getPrefHeight()/2;
        int num = adjMatrix.length;
        int squr = (int)sqrt(num) + 1;

        int yy=y,xx=x;
        for(int i=0; i < squr; i++){
            for(int j=0; j < squr; j++){
                if(num-- <= 0) break;
                addNode(xx,yy);
                yy += 120;
                xDirList.add((double) xx);
                yDirList.add((double) yy);
            }
            xx += 120;
            yy = y;
        }

        //todo not finish

        for(int i=0; i < nodesList.size(); i++){
            for(int j=1; j < nodesList.get(i).size(); j++){
                nodesList.get(i).get(0).setDirX(xDirList.get(nodesList.get(i).get(0).getIndex()));
                nodesList.get(i).get(0).setDirY(yDirList.get(nodesList.get(i).get(0).getIndex()));
                nodesList.get(i).get(j).setDirX(xDirList.get(nodesList.get(i).get(j).getIndex()));
                nodesList.get(i).get(j).setDirY(yDirList.get(nodesList.get(i).get(j).getIndex()));
                drawLine2(nodesList.get(i).get(0), nodesList.get(i).get(j), adjMatrix[nodesList.get(i).get(0).getIndex()][nodesList.get(i).get(j).getIndex()]);
            }
        }

    }

    @FXML
    private void restartButtonHandler() {
        waitingForPlacement = false;
        adjList.clear();
        nodesList.clear();
        graphNodeLinesList.clear();
        xDirList.clear();
        yDirList.clear();
        indexOfGraph = 0;
        isRunning = false;
        customPane.getChildren().clear();
        allInNode.clear();
        allOutNode.clear();
        allGraphState.clear();
        initialize();
    }

    public void loadGraphFromMatrix(int[][] adjMatrix){
        restartButtonHandler();

        // Node List :
        for (int i = 0; i < adjMatrix.length; i++) {
            GraphNode graphNode = new GraphNode(i, xDirList.get(i) , yDirList.get(i));
            LinkedList<GraphNode> tmp = new LinkedList<>();
            tmp.add(graphNode);
            for (int j = 0; j < adjMatrix.length; j++) {
                if(adjMatrix[i][j] != 0 ){
                    GraphNode graphNode1 = new GraphNode(j, xDirList.get(j), yDirList.get(j));
                    tmp.add(graphNode1);
                }
            }
            nodesList.add(tmp);
        }

        // AdjList :
        for (int i = 0; i < adjMatrix.length; i++) {
            LinkedList<Pair<Integer , Integer>> tmp = new LinkedList<>();
            for (int j = 0; j < adjMatrix.length; j++) {
                if(adjMatrix[i][j] != 0) {
                    Pair<Integer , Integer > pair = new Pair<>(j, adjMatrix[i][j]);
                    tmp.add(pair);
                }
            }
            adjList.add(i, tmp);
        }

    }

    private void deleteNode(int index){
        for (int i=0;i<allInNode.get(index).size();i++){
            customPane.getChildren().remove(allInNode.get(index).get(i).second);
        }
        for (int i=0;i<allOutNode.get(index).size();i++){
            customPane.getChildren().remove(allOutNode.get(index).get(i).second);
        }
        allInNode.get(index).clear();
        allOutNode.get(index).clear();

        for (int i = 0; i <allInNode.size() ; i++) {
            for (int j = 0; j <allInNode.get(i).size() ; j++) {
                if (allInNode.get(i).get(j).first.getIndex()==index){
                    allInNode.get(i).remove(j);

                }

            }
        }
        graphNodeLinesList.set(index,null);
        for (int i = 0; i < nodesList.size() ; i++) {
            for (int j = 0; j < nodesList.get(i).size() ; j++) {
                if(nodesList.get(i).get(j).getIndex()==index){
                    nodesList.get(i).remove(j);
                }

            }
        }
        nodesList.get(index).set(index,null);

        for (int i = 0; i <adjList.size() ; i++) {
            for (int j = 0; j <adjList.get(i).size() ; j++) {
                if (adjList.get(i).get(j).first==index){
                    adjList.get(i).remove(j);
                }

            }

        }
        adjList.set(index,null);

    }

    private void setGraphNodeListener(GraphNode graphNode){

        graphNode.setOnMousePressed(event -> {
            if (!isRunning && !waitForLine) {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = graphNode.getLayoutX() - event.getSceneX();
                dragDelta.y = graphNode.getLayoutY() - event.getSceneY();

            }
        });

        graphNode.setOnMouseDragged(event1 -> {
            if (!isRunning && !waitForLine) {
                graphNode.setCursor(Cursor.MOVE);

                dragLineIn(graphNode);
                dragLineOut(graphNode);

                graphNode.setDirX(event1.getSceneX() + dragDelta.x);
                graphNode.setDirY(event1.getSceneY() + dragDelta.y);
            }
        });

        graphNode.setOnMouseReleased(event -> {
            graphNode.setCursor(Cursor.DEFAULT);

            if (!isRunning && event.isDragDetect()) {
                try {
                    waitForLine = true;
                    graphNode.setStyle(NODE_STYLE_SELCTION);
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

            xDirList.set(graphNode.getIndex(), event.getSceneX() + dragDelta.x);
            yDirList.set(graphNode.getIndex(), event.getSceneY() + dragDelta.y);
        });
    }

    private void remove(){
        customPane.getChildren().removeIf(n -> (n instanceof Arrow));
        customPane.getChildren().removeIf(n -> (n instanceof LabelSerializable));
    }

    private void dragLineOut(GraphNode graphNode){
        for(int i=0; i<allOutNode.get(graphNode.getIndex()).size(); i++){

            GraphNode out = allOutNode.get(graphNode.getIndex()).get(i).first;
            Line line = allOutNode.get(graphNode.getIndex()).get(i).second;
            int weight = Integer.parseInt(line.label.getLabelText());
            deleteLine(graphNode, line);

            line = drawLine2(graphNode,out, weight);
            allOutNode.get(graphNode.getIndex()).get(i).second = line;

            for (int j=0; j<allInNode.get(out.getIndex()).size(); j++){
                if(allInNode.get(out.getIndex()).get(j).first == graphNode){
                    allInNode.get(out.getIndex()).get(j).second = line;
                }
            }

        }
    }

    private void dragLineIn(GraphNode graphNode){
//        dragLine(graphNode, allInNode, allOutNode);
        for(int i=0; i<allInNode.get(graphNode.getIndex()).size(); i++){
            GraphNode in = allInNode.get(graphNode.getIndex()).get(i).first;
            Line line = allInNode.get(graphNode.getIndex()).get(i).second;
            int weight = Integer.parseInt(line.label.getLabelText());
            deleteLine(graphNode, line);

            line =  drawLine2(in,graphNode, weight);
            allInNode.get(graphNode.getIndex()).get(i).second =line;

            for (int j=0; j<allOutNode.get(in.getIndex()).size(); j++){
                if(allOutNode.get(in.getIndex()).get(j).first == graphNode){
                    allOutNode.get(in.getIndex()).get(j).second = line;
                }
            }
        }
    }

    private void deleteLine(GraphNode graphNode, Line line){
        customPane.getChildren().remove(line.arrow);
        customPane.getChildren().remove(line.label);
    }

    private void drawLine() {
        if (graphNodeLinesList.size() != 2) return;

        GraphNode graphNode1 = graphNodeLinesList.pop();
        GraphNode graphNode2 = graphNodeLinesList.pop();

        int result = 0;
        for (int i=0; i< allGraphState.get(graphNode1.getIndex()).size(); i++ )
            if(allGraphState.get(graphNode1.getIndex()).get(i).first.getIndex() == graphNode2.getIndex()){
                result = dialog.NumberInputDialogShow("edit the edge weight:");

                customPane.getChildren()
                        .remove(allGraphState.get(graphNode1.getIndex()).get(i).second.arrow);
                customPane.getChildren()
                        .remove(allGraphState.get(graphNode1.getIndex()).get(i).second.label);

                allGraphState.get(graphNode1.getIndex()).remove(i);
                break;
            }
        if(result==0) result = dialog.NumberInputDialogShow("Enter the edge weight:");


        Line line = _drawLine(graphNode1, graphNode2, result);

        allGraphState.get(graphNode1.getIndex()).add(new Pair<>(graphNode2,line));
        allInNode.get(graphNode2.getIndex()).add(new Pair<>(graphNode1,line));
        allOutNode.get(graphNode1.getIndex()).add(new Pair<>(graphNode2,line));

        graphNode1.setStyle(NODE_STYLE_SELCTION);
        graphNode2.setStyle(NODE_STYLE_SELCTION);

        setNodesDefaultColor();
        waitForLine = false;
    }

    private GraphNode getNodeByIndex(int index){
        for (LinkedList<GraphNode> graphNodes : nodesList) {
            if (graphNodes.get(0).getIndex() == index) {
                return graphNodes.get(0);
            }
        }
        return null;
    }

    private Line _drawLine(GraphNode graphNode1, GraphNode graphNode2, int weight){
        Line l = drawLine2(graphNode1,graphNode2,weight);

        Pair<Integer, Integer> temp = new Pair<>(graphNode2.getIndex(), weight);
        adjList.get(graphNode1.getIndex()).add(temp);
        nodesList.get(graphNode1.getIndex()).add(graphNode2);

        return l;
    }

    private Line drawLine2(GraphNode graphNode1, GraphNode graphNode2, int weight){
        Arrow arrow;

        double node1X = graphNode1.getLayoutX()+25, node1Y = graphNode1.getLayoutY()+25;
        double node2X = graphNode2.getLayoutX()+25,node2Y= graphNode2.getLayoutY()+25;


        LabelSerializable label = makeLabel(weight+"", graphNode1, graphNode2, node1X, node1Y, node2X, node2Y);
        final double ALPHA = calculateAlpha(graphNode1, graphNode2);
        arrow = makeArrow(ALPHA, node1X,node1Y, node2X, node2Y);

        customPane.getChildren().add(arrow);
        customPane.getChildren().add(label);


        arrow.toFront();
        graphNode1.toFront();
        graphNode2.toFront();

        return new Line(arrow,label);
    }

    private LabelSerializable makeLabel(String weight, GraphNode graphNode1, GraphNode graphNode2, double node1X, double node1Y, double node2X, double node2Y ){
        LabelSerializable label = new LabelSerializable(weight);
        if(node1Y >= node2Y){
            label.setDirX(((graphNode1.getDirX()+25 + graphNode2.getDirX()+25)/2)  - (abs(node1Y - node2Y)/18) );
            label.setDirY(((graphNode1.getDirY()+25 + graphNode2.getDirY()+25)/2)  - (abs(node1X - node2X)/18) -5 );
        }else if (node1Y < node2Y){
            label.setDirX(((graphNode1.getDirX()+25 + graphNode2.getDirX()+25)/2)    + (abs(node1Y - node2Y)/18) );
            label.setDirY(((graphNode1.getDirY()+25 + graphNode2.getDirY()+25)/2)    + (abs(node1X - node2X)/18) -2 );
        }
        return label;
    }

    private Arrow makeArrow(double ALPHA, double node1X, double node1Y, double node2X, double node2Y){
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
                nodesList.get(integer).get(0).setStyle(NODE_STYLE_SELCTION);
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
        this.indexOfGraph = graphData.getIndex();


        //load node list
        for(int i=0; i < graphData.getNodesList().size(); i++){
            LinkedList <GraphNode> saveData = graphData.getNodesList().get(i);
            nodesList.add(new LinkedList<>());
            for (int j=0; j< graphData.getNodesList().get(i).size(); j++){
                nodesList.get(i).add(new GraphNode(saveData.get(j).getIndex(), saveData.get(j).getDirX(), saveData.get(j).getDirY() ));
            }
        }

        //load allGraphState
        for(int i=0; i < graphData.getAllGraphState().size(); i++){
            LinkedList <Pair<GraphNode, Line>> saveData = graphData.getAllGraphState().get(i);
            allGraphState.add(new LinkedList<>());
            for (int j=0; j< graphData.getAllGraphState().get(i).size(); j++){
                GraphNode graphNode = new GraphNode(saveData.get(j).first.getIndex(), saveData.get(j).first.getDirX(), saveData.get(j).first.getDirY() );

                LabelSerializable label = new LabelSerializable(saveData.get(j).second.label.getLabelText());
                label.setDirX(saveData.get(j).second.label.getDirX());
                label.setDirY(saveData.get(j).second.label.getDirY());

                Line line = new Line(saveData.get(j).second.arrow, label);

                Pair<GraphNode,Line> pair = new Pair<>(graphNode,line);
                allGraphState.get(i).add(new Pair<>(graphNode,line));
            }
        }

        //load allInNode
        for(int i=0; i < graphData.getAllInNode().size(); i++){
            LinkedList <Pair<GraphNode, Line>> saveData = graphData.getAllInNode().get(i);
            allInNode.add(new LinkedList<>());
            for (int j=0; j< graphData.getAllInNode().get(i).size(); j++){
                GraphNode graphNode = new GraphNode(saveData.get(j).first.getIndex(), saveData.get(j).first.getDirX(), saveData.get(j).first.getDirY() );

                LabelSerializable label = new LabelSerializable(saveData.get(j).second.label.getLabelText());
                label.setDirX(saveData.get(j).second.label.getDirX());
                label.setDirY(saveData.get(j).second.label.getDirY());

                Line line = new Line(saveData.get(j).second.arrow, label);

                Pair<GraphNode,Line> pair = new Pair<>(graphNode,line);
                allInNode.get(i).add(new Pair<>(saveData.get(j).first, saveData.get(j).second));
            }
        }

        //load allOutNode
        for(int i=0; i < graphData.getAllOutNode().size(); i++){
            LinkedList <Pair<GraphNode, Line>> saveData = graphData.getAllOutNode().get(i);
            allOutNode.add(new LinkedList<>());
            for (int j=0; j< graphData.getAllOutNode().get(i).size(); j++){
                GraphNode graphNode = new GraphNode(saveData.get(j).first.getIndex(), saveData.get(j).first.getDirX(), saveData.get(j).first.getDirY() );

                LabelSerializable label = new LabelSerializable(saveData.get(j).second.label.getLabelText());
                label.setDirX(saveData.get(j).second.label.getDirX());
                label.setDirY(saveData.get(j).second.label.getDirY());

                Line line = new Line(saveData.get(j).second.arrow, label);

                Pair<GraphNode,Line> pair = new Pair<>(graphNode,line);
                allOutNode.get(i).add(new Pair<>(saveData.get(j).first, saveData.get(j).second));
            }
        }

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

                graphNode1.setStyle(NODE_STYLE_SELCTION);
                graphNode2.setStyle(NODE_STYLE_SELCTION);
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

    private void addNodeListener(){
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
                        addNode(centerX, centerY);

                    }
                });
    }

    private void addNode(double centerX, double centerY){
        xDirList.add(centerX);
        yDirList.add(centerY);
        GraphNode graphNode = new GraphNode(indexOfGraph++, centerX, centerY);

        LinkedList<GraphNode> tmp = new LinkedList<>();
        tmp.add(graphNode);
        nodesList.add(tmp);

        LinkedList<Pair<Integer, Integer>> tmp_2 = new LinkedList<>();
        adjList.add(tmp_2);

        allInNode.add(new LinkedList<>());
        allOutNode.add(new LinkedList<>());

        allGraphState.add(new LinkedList<>());

        setGraphNodeListener(graphNode);
        customPane.getChildren().add(graphNode);
        graphNode.setStyle(NODE_STYLE_DEFULT);
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
            if (path.size()< 2) {
                isRunning = false;
                setAlgorithmButtonsDisable(false);
                thread.stop();
                return;
            }

            for (Integer integer : path) {
                nodesList.get(integer).get(0).setStyle(NODE_STYLE_SELCTION);
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

            while (queue.size() != 0) {
                finalS[0] = queue.poll().getIndex();
                System.out.print(nodesList.get(finalS[0]).get(0).getIndex() + " ");
                nodesList.get(finalS[0]).get(0)
                        .setStyle(NODE_STYLE_SELCTION);

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
        nodesList.get(v).get(0).setStyle(NODE_STYLE_SELCTION);

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

    private double[][] convertAdjListToMatrix(LinkedList<LinkedList<Pair<Integer, Integer>>> adjList){
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

    /**
     * main tcp algorithm button
     */
    @FXML
    private void tspWithAco(){
        // change color to default color  :
        for (LinkedList<GraphNode> graphNodes : nodesList) {
            graphNodes.get(0).setStyle(NODE_STYLE_DEFULT);
        }
        int source = tspAlgorithmGetSource();
        double [][] distancesMatrix = convertAdjListToMatrix(adjList);
        AcoTsp acoTsp = new AcoTsp(source , distancesMatrix);
        double[] path = acoTsp.getResult();
        System.out.println(acoTsp.getTotalCost());
        for (int i = 0; i < path.length; i++) {
            if(i == path.length - 1 ) System.out.println((int)path[i]);
            else  System.out.print((int) path[i] + "--->");
        }
        acoPathColoring(path);
    }

    /**
     * coloring the result
     * @param path path
     */
    private void acoPathColoring(double[] path){
        thread = new Thread(() -> {
            isRunning = true;
            setAlgorithmButtonsDisable(true);
            btnDJT.setDisable(false);


            if (path.length< 2) {
                isRunning = false;
                setAlgorithmButtonsDisable(false);
                thread.stop();
                return;
            }

            for (double v : path) {
                nodesList.get((int)v).get(0).setStyle(NODE_STYLE_SELCTION);
                MakeDelay();
            }
            setAlgorithmButtonsDisable(false);
            isRunning = false;
        });
        thread.start();
    }

}