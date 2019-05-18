package com.graphAlgorithm.model;

import com.graphAlgorithm.view.other.Pair;

import java.util.LinkedList;

public class ShortestPathDp {
    private int[][] dataMatrix ;
    private int source;

    public int[][] convertAdjListToMatrix(LinkedList<LinkedList<Pair<Integer,Integer>>> adjList){
        this.dataMatrix = new int[adjList.size()][adjList.size()];

        //initialise
        for (int i=0; i<adjList.size(); i++){
            for (int j = 0; j < adjList.size(); j++) {
                dataMatrix[i][j] = 0 ;
            }
        }

        // filling the values of matrix with adjList :
        for (int i = 0; i < adjList.size() ; i++) {
            for (int j = 0; j < adjList.get(i).size(); j++) {
                Pair<Integer , Integer> tmp = adjList.get(i).get(j);
                dataMatrix[i][tmp.getFirst()] = tmp.getSecond();
            }
        }

        return this.dataMatrix ;
    }
}
