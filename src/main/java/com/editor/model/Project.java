package com.editor.model;

import java.util.ArrayList;
import java.util.List;

public class Project {
    private final FieldLibrary fieldLibrary = new FieldLibrary();
    private final FieldLibrary resultLibrary = new FieldLibrary();
    private final List<TestSuite> testSuites = new ArrayList<>();

    public FieldLibrary getFieldLibrary() { return fieldLibrary; }
    public FieldLibrary getResultLibrary() { return resultLibrary; }
    public List<TestSuite> getTestSuites() { return testSuites; }
}
