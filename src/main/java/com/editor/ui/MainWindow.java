package com.editor.ui;

import com.editor.export.ExporterRegistry;
import com.editor.model.Project;

import javax.swing.*;

public class MainWindow extends JFrame {

    public MainWindow(Project project, ExporterRegistry registry) {
        setTitle("Test Case Editor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Field Library", new FieldLibraryPanel(project.getFieldLibrary()));
        tabs.addTab("Test Suites", new TestSuitesPanel(project, registry));

        add(tabs);
    }
}
