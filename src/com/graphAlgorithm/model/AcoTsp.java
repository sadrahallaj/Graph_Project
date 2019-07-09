package com.graphAlgorithm.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.Signature;
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
    private double vaporization ;
    private int indexOfBestAnt ;
    private int SIZE ;

    public AcoTsp(int source, double[][] distanceMatrix) {
        this.source = source;
        this.indexOfBestAnt = source;
        this.distancesMatrix = distanceMatrix;
        this.SIZE = distanceMatrix.length;

        if (SIZE < 45){
            this.alpha = SIZE*2;
            this.beta = SIZE/3;
            this.antNumber = SIZE;
            this.iteration = SIZE;
        }else{
            this.alpha = 45 ;
            this.beta = 45 / 2;
            this.antNumber = SIZE - SIZE/10;
            this.iteration = SIZE;
        }

        this.vaporization = 0.6;

        // initial pheromoneMatrix : 
        // for all edges pheromone set to 1 at first 
        pheromoneMatrix = new double[distancesMatrix.length][distancesMatrix.length];
        for (int i = 0; i < distancesMatrix.length; i++) {
            for (int j = 0; j < SIZE; j++) {
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
//            printAntsPath();

            //update pheromoneMatrix :
            for (int j = 0; j < pheromoneMatrix.length; j++) {
                for (int k = 0; k < pheromoneMatrix.length; k++) {
                    // update pheromone for edge j , k :
                    // we should know if antk has travel throw j , k ?
                    double pheromono = pheromoneMatrix[j][k] ;

                    // to check if and i , has traver from j to k
                    for (int l = 0; l < antNumber; l++) {
                        if(isAntHasGoneThrowEdge(j, k , l )){
                            // new value for pheromone
                            pheromono = (1-vaporization) * pheromono + 1.0 / totalCostOfArray(pathForEachAnt[l]);
                        }
                    }
                    // update pheromone
                    pheromoneMatrix[j][k] = pheromono ;
                }
            }
//            printpheromoneMatrix();

        }

        indexOfBestAnt = findTheBestPath(pathForEachAnt);

    }

    // find the best path at the end of the iterations
    private int findTheBestPath(double[][]pathForEachAnt) {
        int indexOfAnt = source ;
        int totalCostOfEachAnt = Integer.MAX_VALUE ;
        for (int i = 0; i < pathForEachAnt.length; i++) {
            int cost = 0 ;
            for (int j = 0; j < pathForEachAnt[i].length - 1; j++) {
                double first = pathForEachAnt[i][j];
                double second = pathForEachAnt[i][j+1];
                cost += distancesMatrix[(int)first][(int)second];
            }
            if(cost <= totalCostOfEachAnt){
                totalCostOfEachAnt = cost ;
                indexOfAnt = i ;
            }
        }

        return indexOfAnt ;
    }

    public void printpheromoneMatrix(){
        System.out.println();
        for (int i = 0; i < pheromoneMatrix.length; i++){
            for (int j = 0; j < pheromoneMatrix.length; j++){
                System.out.printf("%f ",pheromoneMatrix[i][j]);
            }
            System.out.println();
        }

    }

    public void printAntsPath(){
        System.out.println();
        for (int i = 0; i < pathForEachAnt.length; i++){
            for (int j = 0; j < pathForEachAnt[i].length; j++){
                System.out.print((int)pathForEachAnt[i][j] + " ");
            }
            System.out.println();
        }

    }

   // Lk
    private double totalCostOfArray(double[] array) {
        double cost = 0.0;
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
//        System.out.println("total cost : " +  getTotalCost() + "\n");
        return pathForEachAnt[indexOfBestAnt];
    }

    public double getTotalCost(){
        return totalCostOfArray(pathForEachAnt[indexOfBestAnt]);
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
            if (distancesMatrix[j][i] != 0 && !visited[i]) vertexies.add(i);
        }

        // ∑Ʈα (1/n) β : maxraj loop :
        double maxraj = 0;
        for (int k = 0; k < vertexies.size(); k++) { // distancesMatrix[j].length
            int nextVertex = vertexies.get(k);
            double taw = pheromoneMatrix[j][nextVertex];
            if (/*pheromoneMatrix[j][k] != 0*/ true){
                maxraj += Math.pow(taw , alpha) * Math.pow(visibilityOfEdgeMatrix[j][vertexies.get(k)] , beta);
            }
        }

        LinkedList<Double> probiibltyOfEachEdge = new LinkedList<>();

        // Ʈα (1/n) β / ∑Ʈα (1/n) β
        for (int k = 0; k < vertexies.size(); k++) {
            int nextVertex = vertexies.get(k);
            double sorat = Math.pow(pheromoneMatrix[j][nextVertex] , alpha) * Math.pow(visibilityOfEdgeMatrix[j][nextVertex] , beta);
            probiibltyOfEachEdge.add(sorat / maxraj);
        }

        return vertexies.get(findProbabilityInLinkedList(probiibltyOfEachEdge));
//        return vertexies.get(findMaxInInLinkedList(probiibltyOfEachEdge));
    }

    private int findProbabilityInLinkedList(LinkedList<Double> list){
        int it = 0;
        int[] ProbablyList = new int[100];
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i)*100 - 1; j++){
                ProbablyList[it++] = i;
                if (it == 100) break;
            }
        }
        return ProbablyList[(int)(Math.random()*99)];
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


    public static void Test(String testPath) throws FileNotFoundException {
        int n,b;
        Scanner input = new Scanner(new File(testPath));
        n = input.nextInt();
        b = input.nextInt();
        double[][] distance = new double[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++){
                distance[i][j] = input.nextDouble();}

        double sum=0;
        for (int k = 0; k < 100; k++){
            AcoTsp acoTsp = new AcoTsp(0,distance);
            acoTsp.getResult();
            sum += acoTsp.getTotalCost();
            if (k%10==0)System.out.print(".");
        }
        System.out.println();
        double avreg = sum/100;
        System.out.println("best value is \'"+b+"\' and avr is =>" + avreg +" ===> \"%" + (int)((b/avreg)*100) + "\" ");
    }

    public static void singleTest(String testPath) throws FileNotFoundException {
        int n,b;
        Scanner input = new Scanner(new File(testPath));
        n = input.nextInt();
        b = input.nextInt();
        double[][] distance = new double[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++){
                distance[i][j] = input.nextDouble();}

        long start = System.currentTimeMillis();

        AcoTsp acoTsp = new AcoTsp(0,distance);
        acoTsp.getResult();

        long end = System.currentTimeMillis();
        float sec = (end - start) / 1000F; System.out.println(sec + " seconds");
        System.out.println("single Test: best value is \'"+b+"\' and avr is =>" + acoTsp.getTotalCost() +" ===> \"%" + (int)((b/acoTsp.getTotalCost())*100) + "\" ");
    }

    public static void main(String[] args) throws FileNotFoundException {
        Test("src/source/test1.txt");
        Test("src/source/test2.txt");
        Test("src/source/test3.txt");

        singleTest("src/source/test1.txt");
        singleTest("src/source/test2.txt");
        singleTest("src/source/test3.txt");
        singleTest("src/source/test4.txt");
//        singleTest("src/source/test5.txt");
    }
}
