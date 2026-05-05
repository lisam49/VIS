package com.vis.view;

import javafx.scene.Node;

public abstract class BaseView {


    protected static final String BLACK_PEARL = "#0A0A0A";
    protected static final String DARK_SLATE = "#1F1F2F";
    protected static final String ROSE_GOLD = "#B76E79";
    protected static final String SOFT_PINK = "#FF9EB5";
    protected static final String PEARL_WHITE = "#EAEAEA";
    protected static final String ROSE_TINT = "#FFF0F3";

    // Abstract method - each view must implement this
    public abstract Node build();
}