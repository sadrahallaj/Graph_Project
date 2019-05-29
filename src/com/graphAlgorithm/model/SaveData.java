package com.graphAlgorithm.model;


import com.graphAlgorithm.view.other.Pair;
import com.graphAlgorithm.view.other.graphNode;

import java.io.Serializable;
import java.util.LinkedList;

public class SaveData implements Serializable {

    private int index ;
    private LinkedList<Double> xDir ;
    private LinkedList<Double> yDir ;
    private LinkedList<LinkedList<graphNode>> nodesList ;
    private LinkedList<LinkedList<Pair<Integer, Integer>>> adjList ;

    public SaveData(
            LinkedList<LinkedList<Pair<Integer, Integer>>> adjList ,
            LinkedList<Double> xDir, LinkedList<Double> yDir,
            LinkedList<LinkedList<graphNode>> nodesList , int index ) {

        this.index = index;
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

    public LinkedList<Double> getyDir() {
        return yDir;
    }

    public LinkedList<LinkedList<graphNode>> getNodesList() {
        return nodesList;
    }

    public LinkedList<LinkedList<Pair<Integer, Integer>>> getAdjList() {
        return adjList;
    }

}