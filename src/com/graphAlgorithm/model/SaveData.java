package com.graphAlgorithm.model;


import com.graphAlgorithm.view.other.Pair;
import com.graphAlgorithm.view.other.graphNode;

import java.util.LinkedList;

public class SaveData {
    private Thread thread ;
    private boolean waitingForPlacement ;
    private int index ;
    private boolean finished  ;
    private LinkedList<Double> xDir ;
    private LinkedList<Double> yDir ;
    private LinkedList<LinkedList<graphNode>> nodesList ;
    private LinkedList<LinkedList<Pair<Integer, Integer>>> adjList ;

    public SaveData(LinkedList<LinkedList<Pair<Integer, Integer>>> adjList , LinkedList<Double> xDir, LinkedList<Double> yDir, LinkedList<LinkedList<graphNode>> nodesList , int index ) {
        this.thread = thread;
        this.waitingForPlacement = waitingForPlacement;
        this.index = index;
        this.finished = finished;
        this.xDir = xDir;
        this.yDir = yDir;
        this.nodesList = nodesList;
        this.adjList = adjList;
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

    public void setxDir(LinkedList<Double> xDir) {
        this.xDir = xDir;
    }

    public LinkedList<Double> getyDir() {
        return yDir;
    }

    public void setyDir(LinkedList<Double> yDir) {
        this.yDir = yDir;
    }

    public LinkedList<LinkedList<graphNode>> getNodesList() {
        return nodesList;
    }

    public void setNodesList(LinkedList<LinkedList<graphNode>> nodesList) {
        this.nodesList = nodesList;
    }

    public LinkedList<LinkedList<Pair<Integer, Integer>>> getAdjList() {
        return adjList;
    }

    public void setAdjList(LinkedList<LinkedList<Pair<Integer, Integer>>> adjList) {
        this.adjList = adjList;
    }
}
