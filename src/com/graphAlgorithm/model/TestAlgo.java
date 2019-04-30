package com.graphAlgorithm.model;

import com.graphAlgorithm.view.other.Pair;

import java.util.LinkedList;

public class TestAlgo {
    public static void main(String[] args){
        LinkedList<LinkedList<Pair<Integer,Integer>>> adjList = new LinkedList<>();
        Pair<Integer,Integer> pair1 = new Pair(1 , 3 );
        Pair<Integer,Integer> pair2 = new Pair(2  , 1 );
        Pair<Integer,Integer> pair3 = new Pair(3 ,  1);
        Pair<Integer,Integer> pair4 = new Pair(2 , 2 );
        Pair<Integer,Integer> pair5 = new Pair(Integer.MIN_VALUE , Integer.MIN_VALUE );

        LinkedList<Pair<Integer , Integer>> list1  = new LinkedList<>();
        list1.add(pair1);
        list1.add(pair2);
        LinkedList<Pair<Integer , Integer>> list2  = new LinkedList<>();
        list2.add(pair5);
        LinkedList<Pair<Integer , Integer>> list3  = new LinkedList<>();
        list3.add(pair3);
        LinkedList<Pair<Integer , Integer>> list4  = new LinkedList<>();
        list4.add(pair4);

        adjList.add(list1);
        adjList.add(list2);
        adjList.add(list3);
        adjList.add(list4);

        DijkstrasAlgorithm dijkstrasAlgorithm = new DijkstrasAlgorithm();
        dijkstrasAlgorithm.algorithm(adjList , 0);
//        System.out.println(dijkstrasAlgorithm.data);
        dijkstrasAlgorithm.shortesPath(3);
    }



}
