package com.graphAlgorithm.model;

import com.graphAlgorithm.view.other.Pair;
import java.util.LinkedList;

public class DijkstraAlgorithm {

    private LinkedList<Pair<Integer,Integer>> data = new LinkedList<>();
    private int source;

    public LinkedList<Pair<Integer,Integer>> algorithm(
            LinkedList<LinkedList<Pair<Integer,Integer>>> adjList, int source){

        this.source = source;

        int s = source ,visNum = 0;
        int[] dist =new int[adjList.size()], prev =new int[adjList.size()];
        boolean[] vis =new boolean[adjList.size()];

        //initialise
        for (int i=0; i<adjList.size(); i++){
            dist[i] = Integer.MAX_VALUE;
            prev[i] = -1;
            vis[i] = false;
        }
        dist[s] = 0;

        while(true){
            //check all the source edges
            for (int i=0; i<adjList.get(s).size(); i++){
                Pair<Integer,Integer> now = adjList.get(s).get(i);
                //update data table
                if (dist[now.first] > now.second + dist[s]){
                    dist[now.first] = now.second + dist[s];
                    prev[now.first] = s;
                }
            }
            visNum++; vis[s] = true;

            //break condition
            if (visNum == adjList.size()) break;

            //find the next source
            int index = 0, min = Integer.MAX_VALUE;
            for (int i=0; i<adjList.size(); i++){
                if (!vis[i] && dist[i] < min){
                    min = dist[i];
                    index = i;
                }
            }
            s = index;
        }

        LinkedList<Pair<Integer,Integer>> data = new LinkedList<>();
        for (int i=0; i<adjList.size(); i++)
            data.add(new Pair<>(dist[i],prev[i]));

        this.data = data;
        return data;
    }

    public LinkedList<Integer> shortestPath(int d){
        int currentVertex = d ;
        LinkedList<Integer> path = new LinkedList<>();
        path.add(d);
        while (currentVertex != source){
            if(data.get(currentVertex).getSecond() == -1 ) break;
            currentVertex = data.get(currentVertex).getSecond();
            path.add(currentVertex);
        }
        return reverseLinkedList(path);
    }

    private static LinkedList<Integer> reverseLinkedList(LinkedList<Integer> llist)
    {
        LinkedList<Integer> revLinkedList = new LinkedList<>();
        for (int i = llist.size() - 1; i >= 0; i--) {
            revLinkedList.add(llist.get(i));
        }
        return revLinkedList;
    }

}