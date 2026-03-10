package com.editor.model;

import java.util.ArrayList;
import java.util.List;

public class ResultColumn {
    private String name;
    private final List<String> allowedValues = new ArrayList<>();

    public ResultColumn(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getAllowedValues() { return allowedValues; }

    @Override
    public String toString() { return name; }
}
