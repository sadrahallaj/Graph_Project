package com.graphAlgorithm.model;

import com.graphAlgorithm.view.other.Pair;

import java.util.LinkedList;

public class DijkstrasAlgorithm {
    public LinkedList<Pair<Integer,Integer>> data;
    public int source;

    public void algorithm(LinkedList<LinkedList<Pair<Integer,Integer>>> adjList, int source){
        this.source = source;

        //https://www.youtube.com/watch?v=k1kLCB7AZbM

        LinkedList<Pair<Integer,Integer>> data = new LinkedList<>();
        this.data = data;
        Boolean[] visitedVertices = new Boolean[adjList.size()];
//        creating empty linklist(data):
        for (int i = 0; i < adjList.size() ; i++) {
            data.add(new Pair<>(Integer.MAX_VALUE , -1));
            visitedVertices[i] = false ;
        }
        data.get(source).setFirst(0);

        for (int i = 0; i < adjList.get(source).size() ; i++) {
            Pair tmp = adjList.get(source).get(i);
            data.get((Integer) tmp.getFirst()).setFirst((Integer) tmp.getSecond());
            data.get((Integer) tmp.getFirst()).setSecond(source);
        }
        visitedVertices[source] = true ;



        boolean run =  true ;
        while(run){
            //search
            int minValue = Integer.MAX_VALUE ;
            int currentIndex = -1 ;
            for (int i = 0; i <data.size() ; i++) {
                if(visitedVertices[i] || i == source ) continue;
                if(data.get(i).getFirst() < minValue){
                    minValue = data.get(i).getFirst();
                    currentIndex = i ;
                }
            }

            for (int i = 0; i < adjList.get(currentIndex).size()  ; i++) {
                if(adjList.get(currentIndex).getFirst().getFirst() == Integer.MIN_VALUE) break;
                Pair tmp = adjList.get(currentIndex).get(i);
                if((Integer) tmp.getSecond() + data.get(currentIndex).getFirst() < data.get((Integer) tmp.getSecond()).getFirst()){
                    data.get((Integer) tmp.getFirst()).setFirst((Integer) tmp.getSecond() + data.get(currentIndex).getFirst());
                    data.get((Integer) tmp.getFirst()).setSecond(currentIndex);
                }
            }

            visitedVertices[currentIndex] = true ;

            int falseNumber = 0 ;
            for (boolean b:visitedVertices) {
                if(b == false ) falseNumber++ ;
            }
            if(falseNumber == 0 ) run = false ;
        }
    }

    public LinkedList<Integer> shortesPath(int d){

        //todo
        int currentVertex = d ;
        LinkedList<Integer> path = new LinkedList<>();
        path.add(d);
        while (currentVertex != source){
           if(data.get(currentVertex).getSecond() == -1 ) break;
           currentVertex = data.get(currentVertex).getSecond();
           path.add(currentVertex);
        }
        return path;
    }


}
