package com.editor.model;

import java.util.ArrayList;
import java.util.List;

public class Project {
    private final FieldLibrary fieldLibrary = new FieldLibrary();
    private final List<TestSuite> testSuites = new ArrayList<>();

    public FieldLibrary getFieldLibrary() { return fieldLibrary; }
    public List<TestSuite> getTestSuites() { return testSuites; }
}
