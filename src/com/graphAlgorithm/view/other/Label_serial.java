package com.graphAlgorithm.view.other;


import javafx.scene.Node;
import javafx.scene.control.Label;

import java.io.Serializable;

public class Label_serial extends Label implements Serializable {
    public Label_serial() {
    }

    public Label_serial(String text) {
        super(text);
    }

    public Label_serial(String text, Node graphic) {
        super(text, graphic);
    }
}
