package com.graphAlgorithm.model;

import com.graphAlgorithm.view.other.Pair;

import java.util.LinkedList;

public class TestAlgo {
    public static void main(String[] args){
        LinkedList<LinkedList<Pair<Integer,Integer>>> adjList = new LinkedList<>();
        LinkedList<Pair<Integer,Integer>> tmp = new LinkedList<>();

        tmp.add(new Pair<>(1,1));
        tmp.add(new Pair<>(2,2));
        adjList.add(tmp);

        tmp = new LinkedList<>();
        tmp.add(new Pair<>(Integer.MIN_VALUE, Integer.MIN_VALUE));
        adjList.add(tmp);

        tmp = new LinkedList<>();
        tmp.add(new Pair<>(3,1));
        tmp.add(new Pair<>(4,3));
        adjList.add(tmp);

        tmp = new LinkedList<>();
        tmp.add(new Pair<>(Integer.MIN_VALUE,Integer.MIN_VALUE));
        adjList.add(tmp);

        tmp = new LinkedList<>();
        tmp.add(new Pair<>(3,4));
        adjList.add(tmp);

        DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm();
        LinkedList<Pair<Integer,Integer>> b  = dijkstraAlgorithm.algorithm(adjList , 0);
//        System.out.println(dijkstraAlgorithm.data);
        LinkedList<Integer> a = dijkstraAlgorithm.shortestPath(3);
        for(int inA : a ){
            System.out.println(inA);
        }
    }



}
