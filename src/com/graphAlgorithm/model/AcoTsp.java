package com.graphAlgorithm.model;

import java.util.LinkedList;

public class AcoTsp {
    private double[][] pheromoneMatrix ;
    private double[][] distancesMatrix ;
    private double[][] visibilityOfEdgeMatrix ;
    private int source;
    private double[][] pathForEachAnt ;
    private double [] pathCostForEachAnt ;
    
    public AcoTsp(int source , double[][] distanceMatrix){
        this.source = source ;
        this.distancesMatrix = distanceMatrix ;

        // initial pheromoneMatrix : 
        // for all edges pheromone set to 1 at first 
        pheromoneMatrix = new double[distancesMatrix.length][distancesMatrix.length];
        for (int i = 0; i < distancesMatrix.length; i++) {
            for (int j = 0; j < distanceMatrix.length; j++) {
                pheromoneMatrix[i][j] = 1 ;
            }
        }

        // initial visibilityOfEdgeMatrix : 
        // set 1 / dij :
        visibilityOfEdgeMatrix = new double[distancesMatrix.length][distancesMatrix.length];
        for (int i = 0; i < distancesMatrix.length; i++) {
            for (int j = 0; j < distancesMatrix.length; j++) {
                if(distancesMatrix[i][j] == 0 ) visibilityOfEdgeMatrix[i][j] = 0 ;
                else visibilityOfEdgeMatrix[i][j] = 1 / distancesMatrix[i][j];
            }
        }

        // initial pathForEachAnt :
        // for all ants set to 0 at first
        pathForEachAnt = new double[distancesMatrix.length][distancesMatrix.length + 1 ];
        for (int i = 0; i < distancesMatrix.length ; i++) {
            for (int j = 0; j < distancesMatrix.length +1  ; j++) {
                pathForEachAnt[i][j] = i ;
            }
        }

        // initial pathCostForEachAnt
        // for all ants set to 0 at first
        pathCostForEachAnt = new double[distancesMatrix.length];
        for (int i = 0; i < pathCostForEachAnt.length; i++) {
            pathCostForEachAnt[i] = 0 ;
        }
    }

    private void algorithm(){
        // here should update these things :
        // 1 - pheromoneMatrix
        // 2 - pathForEachAnt
        // 3 - pathCostForEachAnt
    }


    private int totalCostOfList(LinkedList<Integer> antList){
        int cost = 0 ;
        int currentVertex =  antList.getFirst() ;
        for (int i = 1; i < antList.size() ; i++) {
            int nextVertex = antList.get(i);
            cost += distancesMatrix[currentVertex][nextVertex];
        }
        return cost ;
    }

    private double[] getResult(){
        return pathForEachAnt[this.source];
    }


}
