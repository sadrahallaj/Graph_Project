package com.graphAlgorithm.model;

import java.io.*;

public class FileIO {

    //to write btree class to file
    public static void writeAnObjectToFile(String filePath, Object o) throws IOException {
        FileOutputStream f = new FileOutputStream(filePath);
        ObjectOutputStream oos = new ObjectOutputStream(f);
        oos.writeObject(o);
    }

    //to read btree class from file
    public static Object readAnObjectFromFile(String filePath) throws IOException, ClassNotFoundException {
        InputStream file = new FileInputStream(filePath);
        InputStream buffer = new BufferedInputStream(file);
        ObjectInput input = new ObjectInputStream(buffer);
        return input.readObject();
    }
}
