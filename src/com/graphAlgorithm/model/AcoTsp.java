package com.graphAlgorithm.model;

import java.util.LinkedList;
import java.util.Scanner;

public class AcoTsp {
    private double[][] pheromoneMatrix;
    private double[][] distancesMatrix;
    private double[][] visibilityOfEdgeMatrix;
    private int source;
    private double[][] pathForEachAnt;
    private double[] pathCostForEachAnt;
    private double alpha ;
    private double beta ;
    private int iteration;
    private int antNumber ;

    public AcoTsp(int source, double[][] distanceMatrix , double alpha , double beta , int iteration , int antNumber) {
        this.source = source;
        this.distancesMatrix = distanceMatrix;
        this.alpha = alpha ;
        this.beta = beta ;
        this.iteration = iteration ;
        this.antNumber = antNumber ;

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
        pathForEachAnt = new double[antNumber][distancesMatrix.length + 1];
        for (int i = 0; i < antNumber; i++) {
            for (int j = 0; j < distancesMatrix.length + 1; j++) {
                pathForEachAnt[i][j] = i;
            }
        }

        // initial pathCostForEachAnt
        // for all ants set to 0 at first
        pathCostForEachAnt = new double[antNumber];
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
        for (int i = 0; i < iteration; i++) {
            // loop for each ant :
            for (int j = 0; j < antNumber; j++) {
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

                    // to check if and i , has traver from j to k
                    for (int l = 0; l < antNumber; l++) {
                        if(isAntHasGoneThrowEdge(j, k , l )){
                            pheromono += 1.0 / totalCostOfArray(pathForEachAnt[l]);
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

    public double[] getResult() {
        algorithm();
//        System.out.println(totalCostOfArray(pathForEachAnt[this.source]));
        System.out.println("total cost : " +  totalCostOfArray(pathForEachAnt[source]) + "\n");
        return pathForEachAnt[this.source];
    }

    private int countUnvisitedVertexies(boolean[] visitedArray) {
        int count = 0;
        for (boolean b : visitedArray) {
            if (!b) count++;
        }
        return count;
    }

    // try to find the next vertex from vertex j by ant and consider the visited cities :
    private int findNextVertex(int j, boolean[] visited) {
        LinkedList<Integer> vertexies = new LinkedList<>();
        for (int i = 0; i < distancesMatrix.length; i++) {
            if (distancesMatrix[j][i] != 0 && visited[i] == false) vertexies.add(i);
        }

        // ∑Ʈα (1/n) β : maxraj loop :
        double maxraj = 0;
        for (int k = 0; k < vertexies.size(); k++) { // distancesMatrix[j].length
             double taw = pheromoneMatrix[j][vertexies.get(k)];
            if (pheromoneMatrix[j][k] != 0)
                maxraj += Math.pow(taw , alpha) * Math.pow(visibilityOfEdgeMatrix[j][vertexies.get(k)] , beta);
        }

        LinkedList<Double> probiibltyOfEachEdge = new LinkedList<>();

        // Ʈα (1/n) β / ∑Ʈα (1/n) β
        for (int k = 0; k < vertexies.size(); k++) {
            int nextVertex = vertexies.get(k);
            double sorat = Math.pow(pheromoneMatrix[j][nextVertex] , alpha) * Math.pow(visibilityOfEdgeMatrix[j][nextVertex] , beta);
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
                    (((i + 1 <pathForEachAnt[ant].length)&& pathForEachAnt[ant][i+1] == k )
                            || ((i - 1 >= 0 ) && pathForEachAnt[ant][i - 1 ] == k ))){
                   condition = true ;
            }
        }
        return condition ;
    }

    public static void main(String[] args){
        int n;
        Scanner input = new Scanner(System.in);
        n = input.nextInt();
        double[][] distance = new double[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) distance[i][j] = input.nextDouble();

        AcoTsp acoTsp = new AcoTsp(0,distance,0.7,0.7,5,5);
        double[] path = acoTsp.getResult();
        for (int i = 0; i < path.length; i++) {
            if(i == path.length - 1 ) System.out.println((int)path[i]);
            else  System.out.print((int) path[i] + "--->");
        }
    }
}
