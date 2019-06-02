package com.graphAlgorithm.view.componenets;

import java.io.Serializable;

public class Line implements Serializable {
    public LabelSerializable label;
    public Arrow arrow;

    public Line( Arrow arrow, LabelSerializable label) {
        this.label = label;
        this.arrow = arrow;
    }
}