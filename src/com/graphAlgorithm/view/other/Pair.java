package com.graphAlgorithm.view.other;

public class Pair<T,V> {
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

    public void setFirst(T first) {
        this.first = first;
    }

    public void setSecond(V second) {
        this.second = second;
    }
}
