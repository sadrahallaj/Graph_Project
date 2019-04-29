package com.graphAlgorithm.model;

import com.graphAlgorithm.view.other.Pair;

import java.util.LinkedList;

public class DijkstrasAlgorithm {
    private LinkedList<Pair<Integer,Integer>> data;
    private int source;

    public void algorithm(LinkedList<LinkedList<Pair<Integer,Integer>>> adjList, int source){
        this.source = source;

        //todo

        LinkedList<Pair<Integer,Integer>> data = new LinkedList<>();
        this.data = data;
    }

    public LinkedList<Integer> shortesPath(int d){

        //todo

        return new LinkedList<>();
    }
}
