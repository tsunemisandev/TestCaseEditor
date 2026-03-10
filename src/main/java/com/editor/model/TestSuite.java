package com.editor.model;

import java.util.ArrayList;
import java.util.List;

public class TestSuite {
    private String name;
    private final List<ConditionColumn> conditions = new ArrayList<>();
    private final List<ResultColumn> results = new ArrayList<>();
    private final List<TestCase> cases = new ArrayList<>();

    public TestSuite(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<ConditionColumn> getConditions() { return conditions; }
    public List<ResultColumn> getResults() { return results; }
    public List<TestCase> getCases() { return cases; }

    @Override
    public String toString() { return name; }
}
