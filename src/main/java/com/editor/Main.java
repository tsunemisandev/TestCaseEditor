package com.editor;

import com.editor.export.ExporterRegistry;
import com.editor.export.HtmlExporter;
import com.editor.model.Project;
import com.editor.ui.MainWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ExporterRegistry registry = new ExporterRegistry();
            registry.register(new HtmlExporter());

            Project project = new Project();
            new MainWindow(project, registry).setVisible(true);
        });
    }
}
