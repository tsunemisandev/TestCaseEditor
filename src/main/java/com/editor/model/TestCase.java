package com.editor.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class TestCase {
    private final Map<ConditionColumn, String> conditionValues = new LinkedHashMap<>();
    private final Map<ResultColumn, String> resultValues = new LinkedHashMap<>();

    public Map<ConditionColumn, String> getConditionValues() { return conditionValues; }
    public Map<ResultColumn, String> getResultValues() { return resultValues; }
}
