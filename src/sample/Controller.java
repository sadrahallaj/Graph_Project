package sample;

import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class Controller {

    public Button btnNewNode;
    public Button btnNewLine;
    public Button btnFinish;
    public Button btnBfs;
    public Button btnDfs;
    public Pane customPane;
    public Label label1;
    public boolean waitingForPlacement = false;
    public int index = 0;
    public LinkedList<Node> nodeLine = new LinkedList<>();
    public LinkedList<LinkedList<Node>> nodesList = new LinkedList<>();


    public void btnNewNodeClicked() {
        waitingForPlacement = true;
        customPane.setOnMouseClicked(event -> {
            double centerX = event.getX();
            double centerY = event.getY();
            if (waitingForPlacement) {
                Node node = new Node(index++, centerX, centerY);
                LinkedList<Node> tmp = new LinkedList<>();
                tmp.add(node);
                nodesList.add(tmp);
                node.setOnMouseClicked(event1 -> {
                    node.setStyle("-fx-background-color: red ;-fx-background-radius: 50 ;");
                    nodeLine.add(node);
                    drawLine();

                });
                customPane.getChildren().add(node);
                waitingForPlacement = false;
            }
        });
    }

    private void drawLine() {
        if (nodeLine.size() != 2) return;
        Node node1 = nodeLine.pop();
        Node node2 = nodeLine.pop();
        Line line = new Line(node1.getLayoutX() + 10, node1.getLayoutY() + 10, node2.getLayoutX() + 10, node2.getLayoutY() + 10);
        nodesList.get(node1.getIndex()).add(node2);
        nodesList.get(node2.getIndex()).add(node1);
        node1.setStyle("-fx-border-color: #d0d0d0 ; -fx-border-radius: 50 ; -fx-background-radius: 50 ;");
        node2.setStyle("-fx-border-color: #d0d0d0 ; -fx-border-radius: 50 ; -fx-background-radius: 50 ;");
        customPane.getChildren().add(line);
    }

//    public void newLineclicked() {
//        LinkedList< LinkedList<Integer> > graph = new LinkedList<>();
//        LinkedList <Integer> Temp= new LinkedList<>();
//        Temp.add(10);
//        graph.get(1).add();
//        graph.add(Temp);
//    }

    public void Finishclicked() {
        btnNewNode.setVisible(false);
        btnNewLine.setVisible(false);
        btnFinish.setVisible(false);
        btnDfs.setVisible(true);
        btnBfs.setVisible(true);
    }

    public void bfsclicked() {
        BFS(0);
    }

    public void dfsclicked() {
    }


    void BFS(int s) {
        // Mark all the vertices as not visited(By default
        // set as false)
        boolean visited[] = new boolean[nodesList.size()];

        // Create a queue for BFS
        LinkedList<Node> queue = new LinkedList<>();

        // Mark the current node as visited and enqueue it
        visited[nodesList.get(s).get(0).getIndex()] = true;
        queue.add(nodesList.get(s).get(0));

        while (queue.size() != 0) {
            s = queue.poll().getIndex();
            System.out.print(nodesList.get(s).get(0).getIndex() + " ");
//            nodesList.get(s).get(0).setStyle("-fx-border-color: red ;-fx-background-radius: 50 ;");
            for (int i = 0; i < ; i++) {
                
            }
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Iterator<Node> i = nodesList.get(s).listIterator();
            while (i.hasNext()) {
                Node n = i.next();
                if (!visited[n.getIndex()]) {
                    visited[n.getIndex()] = true;
                    queue.add(n);
                }
            }
        }
    }


}
