package com.graphAlgorithm.model;


import com.graphAlgorithm.view.other.Arrow;
import com.graphAlgorithm.view.other.Label_serial;
import com.graphAlgorithm.view.other.Pair;
import com.graphAlgorithm.view.other.graphNode;
import javafx.scene.control.Label;

import java.io.Serializable;
import java.util.LinkedList;

public class SaveData implements Serializable {
    private int index ;
    private LinkedList<Double> xDir ;
    private LinkedList<Double> yDir ;
    private LinkedList<LinkedList<graphNode>> nodesList ;

    private LinkedList<LinkedList<Pair<Integer, Integer>>> adjList ;

    private LinkedList<Pair<Arrow, Label_serial>> lines = new LinkedList<>();
    public SaveData(LinkedList<LinkedList<Pair<Integer, Integer>>> adjList , LinkedList<Double> xDir, LinkedList<Double> yDir, LinkedList<LinkedList<graphNode>> nodesList , int index, LinkedList<Pair<Arrow, Label_serial>> lines ) {
        this.index = index;
        this.xDir = xDir;
        this.yDir = yDir;
        this.nodesList = nodesList;
        this.adjList = adjList;
        this.lines = lines;
    }

    public int getIndex() {
        return index;
    }

    public LinkedList<Pair<Arrow, Label_serial>> getLines() {
        return lines;
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
