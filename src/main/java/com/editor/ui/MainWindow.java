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

        JTabbedPane libraryTabs = new JTabbedPane();
        libraryTabs.addTab("Conditions", new FieldLibraryPanel(project.getFieldLibrary()));
        libraryTabs.addTab("Results", new FieldLibraryPanel(project.getResultLibrary()));

        JTabbedPane mainTabs = new JTabbedPane();
        mainTabs.addTab("Field Library", libraryTabs);
        mainTabs.addTab("Test Suites", new TestSuitesPanel(project, registry));

        add(mainTabs);
    }
}
