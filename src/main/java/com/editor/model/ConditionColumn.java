package com.editor.model;

public class ConditionColumn {
    private final Group group;
    private final Field field;

    public ConditionColumn(Group group, Field field) {
        this.group = group;
        this.field = field;
    }

    public Group getGroup() { return group; }
    public Field getField() { return field; }

    @Override
    public String toString() {
        return group.getName() + " / " + field.getName();
    }
}
