package com.graphAlgorithm.view.other;


import javafx.scene.Node;
import javafx.scene.control.Label;

import java.io.Serializable;

/**
 * a serializable label to save in graph
 */
public class LabelSerializable extends Label implements Serializable {
    public LabelSerializable() {
    }

    public LabelSerializable(String text) {
        super(text);
    }

    public LabelSerializable(String text, Node graphic) {
        super(text, graphic);
    }
}
