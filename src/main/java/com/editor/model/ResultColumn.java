package com.editor.model;

public class ResultColumn {
    private final Group group;
    private final Field field;

    public ResultColumn(Group group, Field field) {
        this.group = group;
        this.field = field;
    }

    public Group getGroup() { return group; }
    public Field getField() { return field; }
    public String getName() { return field.getName(); }

    @Override
    public String toString() {
        return group.getName() + " / " + field.getName();
    }
}
