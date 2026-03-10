package com.editor.model;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String name;
    private final List<Field> fields = new ArrayList<>();

    public Group(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Field> getFields() { return fields; }

    @Override
    public String toString() { return name; }
}
