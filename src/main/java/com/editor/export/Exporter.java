package com.editor.export;

import com.editor.model.Project;
import com.editor.model.TestSuite;

public interface Exporter {
    String id();
    String displayName();
    String fileExtension();
    String export(TestSuite suite, Project project);
}
