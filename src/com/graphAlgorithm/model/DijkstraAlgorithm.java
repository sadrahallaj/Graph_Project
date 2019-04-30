package com.graphAlgorithm.model;

import com.graphAlgorithm.view.other.Pair;
import java.util.LinkedList;

class DijkstraAlgorithm {

    private LinkedList<Pair<Integer,Integer>> data = new LinkedList<>();
    private int source;

    LinkedList<Pair<Integer,Integer>> algorithm(LinkedList<LinkedList<Pair<Integer,Integer>>> adjList, int source){
        this.source = source;

        LinkedList<Pair<Integer,Integer>> data = new LinkedList<>();
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
                if(!b) falseNumber++ ;
            }
            if(falseNumber == 0 ) run = false ;
        }
        this.data = data;
        return data;
    }

    LinkedList<Pair<Integer,Integer>> algorithm2(LinkedList<LinkedList<Pair<Integer,Integer>>> adjList, int source){
        int s = source;
        int visNum = 0;
        int[] dist =new int[adjList.size()];
        int[] prev =new int[adjList.size()];
        boolean[] vis =new boolean[adjList.size()];

        //initialise
        for (int i=0; i<adjList.size(); i++){
            dist[i] = Integer.MIN_VALUE;
            prev[i] = -1;
            vis[i] = false;
        }
        dist[s] = 0;

        while(true){
            //check all the source edges
            for (int i=0; i<adjList.get(s).size(); i++){
                //check all edges
            }
            visNum++; vis[s] = true;

            //break condition
            if (visNum == adjList.size()) break;

            //find the next source
            for (int i=0; i<adjList.size(); i++){
                int min = Integer.MAX_VALUE, index;
                if (!vis[i] && dist[i] < min){
                    min = dist[i];
                    index = i;
                }
            }

        }

        return null;
    }
    LinkedList<Integer> shortestPath(int d){

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
