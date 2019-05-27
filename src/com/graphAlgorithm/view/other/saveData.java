package com.graphAlgorithm.view.other;


import java.util.LinkedList;

public class saveData {
    private Thread thread ;
    private boolean waitingForPlacement ;
    private int index ;
    private boolean finished  ;
    private LinkedList<Double> xDir ;
    private LinkedList<Double> yDir ;
    private LinkedList<LinkedList<graphNode>> nodesList ;
    private LinkedList<LinkedList<Pair<Integer, Integer>>> adjList ;

    public saveData( LinkedList<LinkedList<Pair<Integer, Integer>>> adjList ,LinkedList<Double> xDir, LinkedList<Double> yDir, LinkedList<LinkedList<graphNode>> nodesList , Thread thread , int index, boolean waitingForPlacement, boolean finished ) {
        this.thread = thread;
        this.waitingForPlacement = waitingForPlacement;
        this.index = index;
        this.finished = finished;
        this.xDir = xDir;
        this.yDir = yDir;
        this.nodesList = nodesList;
        this.adjList = adjList;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public boolean isWaitingForPlacement() {
        return waitingForPlacement;
    }

    public void setWaitingForPlacement(boolean waitingForPlacement) {
        this.waitingForPlacement = waitingForPlacement;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
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
