package com.graphAlgorithm.view.other;

import java.io.Serializable;

/**
 * save the pair of two parameters
 * @param <T> first parameter
 * @param <V> second parameter
 */
public class Pair<T,V> implements Serializable {
    public T first;
    public V second;

    public Pair(T a, V b) {
        this.first = a;
        this.second = b;
    }

    public T getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }
}
