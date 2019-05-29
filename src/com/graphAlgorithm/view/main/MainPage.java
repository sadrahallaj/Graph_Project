package com.graphAlgorithm.view.main;

import com.graphAlgorithm.model.DijkstraAlgorithm;
import com.graphAlgorithm.model.FileIO;
import com.graphAlgorithm.model.TspDynamicProgrammingRecursive;
import com.graphAlgorithm.model.SaveData;
import com.graphAlgorithm.view.other.*;
import com.jfoenix.controls.JFXSlider;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.swing.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

import static com.graphAlgorithm.view.other.MouseClickNotDragDetector.clickNotDragDetectingOn;
import static java.lang.Math.*;


//todo
//خودش را نباید بتواند انتخاب کند
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
    @FXML
    private BorderPane borderPane;

    private Thread thread = new Thread();
    private boolean waitingForPlacement = false;
    private int index = 0;
    private ChoiceDialog choiceDialog;
    private boolean finished = false;
    private LinkedList<graphNode> graphNodeLine = new LinkedList<>();
    private LinkedList<Double> xDir = new LinkedList<>();
    private LinkedList<Double> yDir = new LinkedList<>();
    private LinkedList<LinkedList<graphNode>> nodesList = new LinkedList<>();
    private LinkedList<LinkedList<Pair<Integer, Integer>>> adjList = new LinkedList<>();
    private LinkedList<String> choiceDialogsOptions = new LinkedList<>();

    private ZoomableScrollPane zoomableScrollPane;

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
        setAlgoButtDisble(true);
        waitingForPlacement = true;

        zoomableScrollPane  = new ZoomableScrollPane(customPane);
        borderPane.setCenter(zoomableScrollPane);

        //slider custom text
        setSliderStyle();
        addNode();
    }

    @FXML
    private void saveGraph_Handler(){

        String fileName = "./src/com/graphAlgorithm/view/main/graph.bin" ; // default path

        // ask where to save file :

        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        Stage stage = new Stage();
        File file = fileChooser.showSaveDialog(stage);

        if(file != null){
            fileName = file.getAbsolutePath()+ ".txt";
        }

        SaveData saveData = new SaveData(this.adjList, this.xDir, this.yDir, this.nodesList, this.index);
//        String fileName = "./src/com/graphAlgorithm/view/main/graph.gr";

        try {
            FileIO.writeAnObjectToFile(fileName,saveData);
            System.out.println("Graph saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadGraph_Handler(){

        String fileName = "./src/com/graphAlgorithm/view/main/graph.bin";

        // ask where to open file to load :
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        Stage stage  = new Stage();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
//            mainStage.display(selectedFile);
            fileName =  selectedFile.getAbsolutePath();
        }
        SaveData saveData = null;
        try {
            saveData =  (SaveData)FileIO.readAnObjectFromFile(fileName);
            System.out.println("Graph saved.");

            restartHandler();
            btnFinish.setDisable(false);
            this.adjList = saveData.getAdjList();
            this.xDir = saveData.getxDir();
            this.yDir = saveData.getyDir();
            this.nodesList = saveData.getNodesList();
            this.index = saveData.getIndex();

            // load vertexes :
            for (int i = 0; i < xDir.size(); i++) {
                graphNode node = new graphNode(i, xDir.get(i), yDir.get(i));
                node.setOnMouseClicked(event1 -> {
                    if (!finished) {
                        try {
                            node.setStyle("-fx-background-color: #ff0000; -fx-font-size: 16; -fx-background-radius: 50 ; -fx-pref-height: 50 ; -fx-pref-width: 50");
                            graphNodeLine.add(node);
                            drawLine();
                        } catch (Exception e) {
                            //todo
                            System.out.println(e.toString());
                        }
                    }
                });
                customPane.getChildren().add(node);
            }

            // load lines :
            for (int i = 0; i < adjList.size(); i++) {
                for (int j = 0; j < adjList.get(i).size(); j++) {
                    Arrow arrow = null;

                    graphNode graphNode1 = nodesList.get(i).getFirst();
                    graphNode graphNode2 = nodesList.get(adjList.get(i).get(j).first).get(0);

                    double node1X = xDir.get(graphNode1.getIndex())+25, node1Y = yDir.get(graphNode1.getIndex())+25;
                    double node2X = xDir.get(graphNode2.getIndex())+25,node2Y= yDir.get(graphNode2.getIndex())+25;

                    Label w = new Label(String.valueOf(adjList.get(i).get(j).second));

                    if(node1Y >= node2Y){
                        //todo
                        w.setLayoutX(((node1X + node2X)/2)  - (abs(node1Y - node2Y)/18) );
                        w.setLayoutY(((node1Y + node2Y)/2)  - (abs(node1X - node2X)/18) -5 );
                    }else if (node1Y < node2Y){
                        //todo
                        w.setLayoutX(((node1X + node2X)/2)    + (abs(node1Y - node2Y)/18) );
                        w.setLayoutY(((node1Y + node2Y)/2)    + (abs(node1X - node2X)/18) -2 );
                    }

                    //find alfa degree
                    double alfa = Math.atan( abs(xDir.get(graphNode1.getIndex()) - xDir.get(graphNode2.getIndex()))  /
                            abs(yDir.get(graphNode1.getIndex()) - yDir.get(graphNode2.getIndex())) );
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

                    graphNode1.setStyle("-fx-border-color: #d0d0d0 ;  -fx-font-size: 16; -fx-border-radius: 50 ; -fx-background-radius: 50 ; -fx-pref-height: 50 ; -fx-pref-width: 50");
                    graphNode2.setStyle("-fx-border-color: #d0d0d0 ;  -fx-font-size: 16; -fx-border-radius: 50 ; -fx-background-radius: 50 ;-fx-pref-height: 50 ; -fx-pref-width: 50");
                    customPane.getChildren().add(arrow);
                    customPane.getChildren().add(w);
                    arrow.toBack();
                    graphNode1.toFront();
                    graphNode2.toFront();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException");
        }

    }

    @FXML
    private void restartHandler() {
        waitingForPlacement = false;
        btnFinish.setDisable(true);
        setAlgoButtDisble(true);
        nodesList.clear();
        graphNodeLine.clear();
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
        if (choiceDialog.getSelectedItem() == "random vertex") {
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
        if (choiceDialog.getSelectedItem() == "random vertex") {
            Random rand = new Random();
            DFS_Algorithm(rand.nextInt(nodesList.size()));
        }else{
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
        setNodesDefaultColor();
        int sourceVertex;
        int destinationVertex;

        showFullOptionChoiceDialog("options","Getting source vertex","please select the source vertex : ");
        if (!choiceDialog.showAndWait().isPresent()) return;
        else sourceVertex = Integer.parseInt(choiceDialog.getSelectedItem().toString());

        showFullOptionChoiceDialog("options","Getting destination vertex","please select the destination vertex : ");
        if (!choiceDialog.showAndWait().isPresent()) return;
        else destinationVertex = Integer.parseInt(choiceDialog.getSelectedItem().toString());


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

                    btnFinish.setDisable(false);
                    double centerX = mouseEvent.getX() - 20;
                    double centerY = mouseEvent.getY() - 20;
                    for (int i = 0; i < xDir.size(); i++) {
                        double x = xDir.get(i);
                        double y = yDir.get(i);
                        if (centerX < x + 50 && centerX > x - 50 && centerY < y + 50 && centerY > y - 50) return;
                    }
                    if (mouseEvent.getX() < 25 || mouseEvent.getY() > customPane.getHeight() - 25 || mouseEvent.getX() > customPane.getWidth() - 25 || mouseEvent.getY() < 25)
                        return;
                    else if (waitingForPlacement) {
                        btnFinish.setVisible(true);
                        xDir.add(centerX);
                        yDir.add(centerY);
                        graphNode graphNode = new graphNode(index++, centerX, centerY);
                        LinkedList<graphNode> tmp = new LinkedList<>();
                        LinkedList<Pair<Integer, Integer>> tmp_2 = new LinkedList<>();
                        adjList.add(tmp_2);
                        tmp.add(graphNode);
                        nodesList.add(tmp);
                        graphNode.setOnMouseClicked(event1 -> {
                            if (!finished) {
                                try {
                                    graphNode.setStyle("-fx-background-color: #ff0000; -fx-font-size: 16; -fx-background-radius: 50 ; -fx-pref-height: 50 ; -fx-pref-width: 50");
                                    graphNodeLine.add(graphNode);
                                    drawLine();
                                } catch (Exception e) {
                                    //todo
                                    System.out.println(e.toString());
                                }
                            }
                        });
                        customPane.getChildren().add(graphNode);
                    }
                });
    }

    private void dfs_bfsShowDialog(){
        //set Choice Dialog >>
        choiceDialogsOptions.clear();
        choiceDialogsOptions.clear();
        choiceDialogsOptions.add("random vertex");
        for (LinkedList<graphNode> graphNodes : nodesList) {
            choiceDialogsOptions.add(String.valueOf(graphNodes.get(0).getIndex()));
        }
        showChoiceDialog("Getting source vertex",
                "please select the source vertex : ","");
        // << set Choice Dialog
    }

    private boolean isThreadRunning(){
        return thread.getState() != Thread.State.TERMINATED && thread.getState() != Thread.State.NEW;
    }

    private void resetThread(){
        thread.stop();
        setAlgoButtDisble(false);
        setNodesDefaultColor();
    }

    private void drawLine() {
        Arrow arrow = null;

        if (graphNodeLine.size() != 2) return;
        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle(" ");
        dialog.setContentText("Enter the edge weight:");
        Optional<String> result = dialog.showAndWait();

        graphNode graphNode1 = graphNodeLine.pop();
        graphNode graphNode2 = graphNodeLine.pop();

        double node1X = graphNode1.getLayoutX()+25, node1Y = graphNode1.getLayoutY()+25;
        double node2X = graphNode2.getLayoutX()+25,node2Y= graphNode2.getLayoutY()+25;

        Pair<Integer, Integer> temp = new Pair<>(graphNode2.getIndex(), Integer.parseInt(result.get()));
        adjList.get(graphNode1.getIndex()).add(temp);
        Label w = new Label(String.valueOf(Integer.parseInt(result.get())));

        if(node1Y >= node2Y){
            //todo
            w.setLayoutX(((graphNode1.getLayoutX()+25 + graphNode2.getLayoutX()+25)/2)  - (abs(node1Y - node2Y)/18) );
            w.setLayoutY(((graphNode1.getLayoutY()+25 + graphNode2.getLayoutY()+25)/2)  - (abs(node1X - node2X)/18) -5 );
        }else if (node1Y < node2Y){
            //todo
            w.setLayoutX(((graphNode1.getLayoutX()+25 + graphNode2.getLayoutX()+25)/2)    + (abs(node1Y - node2Y)/18) );
            w.setLayoutY(((graphNode1.getLayoutY()+25 + graphNode2.getLayoutY()+25)/2)    + (abs(node1X - node2X)/18) -2 );
        }

        //find alfa degree
        double alfa = Math.atan( abs(graphNode1.getLayoutX() - graphNode2.getLayoutX())  /
                abs(graphNode1.getLayoutY() - graphNode2.getLayoutY()) );
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


        nodesList.get(graphNode1.getIndex()).add(graphNode2);
//        nodesList.get(graphNode2.getIndex()).add(graphNode1);

        graphNode1.setStyle("-fx-border-color: #d0d0d0 ;  -fx-font-size: 16; -fx-border-radius: 50 ; -fx-background-radius: 50 ; -fx-pref-height: 50 ; -fx-pref-width: 50");
        graphNode2.setStyle("-fx-border-color: #d0d0d0 ;  -fx-font-size: 16; -fx-border-radius: 50 ; -fx-background-radius: 50 ;-fx-pref-height: 50 ; -fx-pref-width: 50");
//        customPane.getChildren().add(line);
        customPane.getChildren().add(arrow);
        customPane.getChildren().add(w);
//        line.toBack();
        arrow.toFront();
        graphNode1.toFront();
        graphNode2.toFront();
    }

    private void setNodesDefaultColor(){
        for (LinkedList<graphNode> graphNodes : nodesList) {
            graphNodes.get(0).setStyle("-fx-background-color: #cfcfcf; -fx-font-size: 16; -fx-background-radius: 50 ; -fx-pref-height: 50 ; -fx-pref-width: 50");
        }
    }

    private void showInfoDialog(String Header, String ContentText){
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(Header);
        a.setContentText(ContentText);
        a.showAndWait();
    }

    private void showChoiceDialog(String Title, String headerText, String contentText) {
        setNodesDefaultColor();
        choiceDialog = new ChoiceDialog(choiceDialogsOptions.get(0), choiceDialogsOptions);

        choiceDialog.setTitle(Title);
        choiceDialog.setHeaderText(headerText);
        choiceDialog.setContentText(contentText);

        choiceDialog.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/source/choice.png"))));
        choiceDialog.setX(customPane.getWidth() / 2 + 320);
        choiceDialog.setY(customPane.getHeight() / 2 - 50);
        Stage stage = (Stage) choiceDialog.getDialogPane().getScene().getWindow();
        javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResourceAsStream("/source/options.png"));
        stage.getIcons().add(image);
    }

    private void showFullOptionChoiceDialog(String Title, String headerText, String contentText){
        choiceDialogsOptions.clear();
        setFullChoiceOption();
        showChoiceDialog(Title, headerText, contentText);
    }

    private void setFullChoiceOption(){
        for (LinkedList<graphNode> graphNodes : nodesList)
            choiceDialogsOptions.add(String.valueOf(graphNodes.get(0).getIndex()));
    }

    private void BFS_Algorithm(int s) {
        int[] finalS = new int[1];
        finalS[0] = s;

        thread = new Thread(() -> {
            setAlgoButtDisble(true);
            btnBfs.setDisable(false);

            boolean[] visited = new boolean[nodesList.size()];

            LinkedList<graphNode> queue = new LinkedList<>();

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

                Iterator<graphNode> i = nodesList.get(finalS[0]).listIterator();
                while (i.hasNext()) {
                    graphNode n = i.next();
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

        for (graphNode n : nodesList.get(v)) {
            if (!visited[n.getIndex()])
                DFSUtil(n.getIndex(), visited);
        }
    }
    // convert adjList to distance matrix :

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

    public void tsp_Dp_Handler(){
        if (isThreadRunning()) {
            resetThread();
            return;
        }
        // reset the colours of vertexes :
        setNodesDefaultColor();
        //dialoge

        int sourceVertex;
        choiceDialogsOptions.clear();
        showFullOptionChoiceDialog("select","select source","select source");
        if (!choiceDialog.showAndWait().isPresent()) return;
        else sourceVertex = Integer.parseInt(choiceDialog.getSelectedItem().toString());

        //stop if nothing selected
        if (sourceVertex == -1) return;

        double [][] distanceMatrix = convertAdjListToMatrix(adjList);
        TspDynamicProgrammingRecursive tsp = new TspDynamicProgrammingRecursive(  sourceVertex , distanceMatrix);

        List<Integer> tspResultList;
        try { tspResultList = tsp.getTour(); }
        catch(Exception e){
            showInfoDialog("Can't be!","there is't a hamiltoni cycle!");
            return;
        }

        thread = new Thread(() -> {
            setAlgoButtDisble(true);
            btnTSP.setDisable(false);

            // colouring the nodes :
            for (Integer integer : tspResultList) {
                nodesList.get(integer).get(0).setStyle("-fx-background-color: #f93f98 ;-fx-background-radius: 50 ;" +
                        " -fx-text-fill: #e5e5e5 ; -fx-font-size: 16; -fx-pref-height: 50 ; -fx-pref-width: 50");
                delay();
            }

            setAlgoButtDisble(false);
        });
        thread.start();
    }

    private void delay(){
        try {
            Thread.sleep((long) (1000 * (1 / slider.getValue())));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
//
//    public void reloadGraph(SaveData obj){
//         restartHandler();
//         this.adjList = obj.getAdjList();
//         this.xDir = obj.getxDir();
//         this.yDir = obj.getyDir();
//         this.nodesList = obj.getNodesList();
//         this.index = obj.getIndex();
//    }
}