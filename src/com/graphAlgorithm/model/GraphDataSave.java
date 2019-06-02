package com.graphAlgorithm.model;


import com.graphAlgorithm.view.componenets.Line;
import com.graphAlgorithm.view.other.Pair;
import com.graphAlgorithm.view.componenets.GraphNode;

import java.io.Serializable;
import java.util.LinkedList;

public class GraphDataSave implements Serializable {
    private int index ;

    private LinkedList<Double> xDir ;
    private LinkedList<Double> yDir ;
    private LinkedList<LinkedList<GraphNode>> nodesList ;
    private LinkedList<LinkedList<Pair<Integer, Integer>>> adjList ;
    private LinkedList<LinkedList<Pair<GraphNode, Line>>> allGraphState;
    private LinkedList<LinkedList<Pair<GraphNode,Line>>>
            allInNode = new LinkedList<>(), allOutNode = new LinkedList<>();
    public GraphDataSave(
            LinkedList<LinkedList<Pair<Integer, Integer>>> adjList ,
            LinkedList<Double> xDir, LinkedList<Double> yDir,
            LinkedList<LinkedList<GraphNode>> nodesList,
            LinkedList<LinkedList<Pair<GraphNode, Line>>> allGraphState
            ,LinkedList<LinkedList<Pair<GraphNode,Line>>> allInNode,
            LinkedList<LinkedList<Pair<GraphNode,Line>>> allOutNode, int index ) {

        this.allInNode = allInNode;
        this.allOutNode = allOutNode;
        this.index = index;
        this.xDir = xDir;
        this.yDir = yDir;
        this.nodesList = nodesList;
        this.adjList = adjList;
        this.allGraphState = allGraphState;
    }

    public LinkedList<LinkedList<Pair<GraphNode, Line>>> getAllInNode() {
        return allInNode;
    }

    public LinkedList<LinkedList<Pair<GraphNode, Line>>> getAllOutNode() {
        return allOutNode;
    }

    public LinkedList<LinkedList<Pair<GraphNode, Line>>> getAllGraphState() {
        return allGraphState;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    public LinkedList<Double> getxDir() {
        return xDir;
    }

    public LinkedList<Double> getyDir() {
        return yDir;
    }

    public LinkedList<LinkedList<GraphNode>> getNodesList() {
        return nodesList;
    }

    public LinkedList<LinkedList<Pair<Integer, Integer>>> getAdjList() {
        return adjList;
    }

}