package com.graphAlgorithm.model;

import java.util.LinkedList;

public class AcoTsp {
    private double[][] pheromoneMatrix;
    private double[][] distancesMatrix;
    private double[][] visibilityOfEdgeMatrix;
    private int source;
    private double[][] pathForEachAnt;
    private double[] pathCostForEachAnt;

    public AcoTsp(int source, double[][] distanceMatrix) {
        this.source = source;
        this.distancesMatrix = distanceMatrix;

        // initial pheromoneMatrix : 
        // for all edges pheromone set to 1 at first 
        pheromoneMatrix = new double[distancesMatrix.length][distancesMatrix.length];
        for (int i = 0; i < distancesMatrix.length; i++) {
            for (int j = 0; j < distanceMatrix.length; j++) {
                if (i == j) pheromoneMatrix[i][j] = 0;
                else pheromoneMatrix[i][j] = 1;
            }
        }

        // initial visibilityOfEdgeMatrix : 
        // set 1 / dij :
        visibilityOfEdgeMatrix = new double[distancesMatrix.length][distancesMatrix.length];
        for (int i = 0; i < distancesMatrix.length; i++) {
            for (int j = 0; j < distancesMatrix.length; j++) {
                if (distancesMatrix[i][j] == 0) visibilityOfEdgeMatrix[i][j] = 0;
                else visibilityOfEdgeMatrix[i][j] = 1 / distancesMatrix[i][j];
            }
        }

        // initial pathForEachAnt :
        // for all ants set to 0 at first
        pathForEachAnt = new double[distancesMatrix.length][distancesMatrix.length + 1];
        for (int i = 0; i < distancesMatrix.length; i++) {
            for (int j = 0; j < distancesMatrix.length + 1; j++) {
                pathForEachAnt[i][j] = i;
            }
        }

        // initial pathCostForEachAnt
        // for all ants set to 0 at first
        pathCostForEachAnt = new double[distancesMatrix.length];
        for (int i = 0; i < pathCostForEachAnt.length; i++) {
            pathCostForEachAnt[i] = 0;
        }
    }

    private void algorithm() {
        // here should update these things :
        // 1 - pheromoneMatrix
        // 2 - pathForEachAnt
        // 3 - pathCostForEachAnt

        // Iteration loop (main loop)
        for (int i = 0; i < distancesMatrix.length; i++) {
            // loop for each ant :
            for (int j = 0; j < distancesMatrix.length; j++) {
                // loop for each edge :
                boolean[] visited = new boolean[distancesMatrix.length];
                for (int k = 0; k < visited.length; k++) {
                    visited[k] = false;
                }
                visited[j] = true;
                int currentVertex = j;
                int counter = 1 ;
                while (countUnvisitedVertexies(visited) != 0) {
                    int nextVertex = findNextVertex(currentVertex, visited);
                    pathForEachAnt[j][counter++] = nextVertex ;
                    visited[nextVertex] = true ;
                    currentVertex = nextVertex ;
                }
                pathForEachAnt[j][counter] = j ;
            }

            //update pheromoneMatrix :
            for (int j = 0; j < pheromoneMatrix.length; j++) {
                for (int k = 0; k < pheromoneMatrix.length; k++) {
                    // update pheromone for edge j , k :
                    // we should know if antk has travel throw j , k ?
                    double pheromono = pheromoneMatrix[j][k] ;
                    for (int l = 0; l < distancesMatrix.length; l++) {
                        if(isAntHasGoneThrowEdge(j, k , i )){
                            pheromono += 1 / totalCostOfArray(pathForEachAnt[i]);
                        }
                    }
                    pheromoneMatrix[j][k] = pheromono ;
                }
            }

        }
    }


   // Lk
    private int totalCostOfArray(double[] array) {
        int cost = 0;
        double currentVertex = array[0];
        for (int i = 1; i < array.length; i++) {
            double nextVertex = array[i];
            cost += distancesMatrix[(int)currentVertex][(int)nextVertex];
            currentVertex = nextVertex;
        }
        return cost;
    }

    private double[] getResult() {
        System.out.println(totalCostOfArray(pathForEachAnt[this.source]));
        return pathForEachAnt[this.source];
    }

    private int countUnvisitedVertexies(boolean[] visitedArray) {
        int count = 0;
        for (int i = 0; i < visitedArray.length; i++) {
            if (visitedArray[i] == false) count++;
        }
        return count;
    }

    private int findNextVertex(int j, boolean[] visited) {
        LinkedList<Integer> vertexies = new LinkedList<>();
        for (int i = 0; i < distancesMatrix.length; i++) {
            if (distancesMatrix[j][i] != 0 && visited[i] != false) vertexies.add(i);
        }

        // ∑Ʈα (1/n) β : maxraj loop :
        double maxraj = 0;
        for (int k = 0; k < distancesMatrix[j].length; k++) {
            double taw = pheromoneMatrix[j][vertexies.get(k)];
            if (pheromoneMatrix[j][k] != 0)
                maxraj += taw * visibilityOfEdgeMatrix[j][vertexies.get(k)];
        }

        LinkedList<Double> probiibltyOfEachEdge = new LinkedList<>();

        // Ʈα (1/n) β / ∑Ʈα (1/n) β

        for (int k = 0; k < vertexies.size(); k++) {
            int nextVertex = vertexies.get(k);
            double sorat = pheromoneMatrix[j][nextVertex] * visibilityOfEdgeMatrix[j][nextVertex];
            probiibltyOfEachEdge.add(sorat / maxraj);
        }

        return vertexies.get(findMaxInInLinkedList(probiibltyOfEachEdge));
    }

    private int findMaxInInLinkedList(LinkedList<Double> list){
        double max = Double.MIN_VALUE ;
        int indexOfMaxValue = 0 ;
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i) > max ){
                max = list.get(i);
                indexOfMaxValue = i ;
            }
        }

        return indexOfMaxValue ;
    }

    // check if ant has travel throw j to k not ?
    private boolean isAntHasGoneThrowEdge(int j , int k , int ant){

        boolean condition = false ;
        for (int i = 0; i < pathForEachAnt[ant].length; i++) {
            if(pathForEachAnt[ant][i] == j &&
                    (((i + 1 <=pathForEachAnt[ant].length)&& pathForEachAnt[ant][i+1] == k )
                            || ((i - 1 >= 0 ) && pathForEachAnt[ant][i - 1 ] == k ))){
                   condition = true ;
            }
        }
        return condition ;
    }
}
