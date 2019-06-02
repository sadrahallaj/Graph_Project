package com.graphAlgorithm.model;

import com.graphAlgorithm.view.componenets.GraphNode;

import java.io.*;
import java.util.LinkedList;

public class FileIO {

    /**
     * save a object in the entered path
     * @param filePath path of the file
     * @param o entered object
     */
    public static void writeAnObjectToFile(String filePath, Object o) throws IOException {
        FileOutputStream f = new FileOutputStream(filePath);
        ObjectOutputStream oos = new ObjectOutputStream(f);
        oos.writeObject(o);
    }

    /**
     * read a object from the path
     * @param filePath file path
     * @return return a object
     */
    public static Object readAnObjectFromFile(String filePath) throws IOException, ClassNotFoundException {
        InputStream file = new FileInputStream(filePath);
        InputStream buffer = new BufferedInputStream(file);
        ObjectInput input = new ObjectInputStream(buffer);
        return setLayout((GraphDataSave) input.readObject());
    }

    private static GraphDataSave setLayout(GraphDataSave graphDataSave){
        LinkedList<LinkedList<GraphNode>> nodesList = graphDataSave.getNodesList();
        for (LinkedList<GraphNode> graphNodes : nodesList)
            for (GraphNode graphNode : graphNodes)
                graphNode.copy();
        return graphDataSave;
    }
}
