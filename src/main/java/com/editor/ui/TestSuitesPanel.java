package com.editor.ui;

import com.editor.export.ExporterRegistry;
import com.editor.model.Project;
import com.editor.model.TestSuite;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TestSuitesPanel extends JPanel {
    private final Project project;
    private final ExporterRegistry registry;
    private final DefaultListModel<TestSuite> listModel = new DefaultListModel<>();
    private final JList<TestSuite> suiteList;
    private final JPanel editorContainer;

    public TestSuitesPanel(Project project, ExporterRegistry registry) {
        this.project = project;
        this.registry = registry;
        setLayout(new BorderLayout());

        suiteList = new JList<>(listModel);
        suiteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suiteList.setFixedCellWidth(180);
        suiteList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showEditor(suiteList.getSelectedValue());
        });

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(new EmptyBorder(8, 8, 8, 4));

        JPanel listToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        JButton addBtn = new JButton("+ Suite");
        addBtn.addActionListener(e -> addSuite());
        JButton deleteBtn = new JButton("- Suite");
        deleteBtn.addActionListener(e -> deleteSuite());
        listToolbar.add(addBtn);
        listToolbar.add(deleteBtn);

        listPanel.add(new JScrollPane(suiteList), BorderLayout.CENTER);
        listPanel.add(listToolbar, BorderLayout.SOUTH);

        editorContainer = new JPanel(new BorderLayout());
        editorContainer.setBorder(new EmptyBorder(8, 4, 8, 8));
        showPlaceholder();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPanel, editorContainer);
        split.setDividerLocation(200);
        split.setResizeWeight(0);
        add(split, BorderLayout.CENTER);

        refreshList();
    }

    private void addSuite() {
        String name = JOptionPane.showInputDialog(this, "Suite name:", "New Suite", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.isBlank()) {
            TestSuite suite = new TestSuite(name.trim());
            project.getTestSuites().add(suite);
            refreshList();
            suiteList.setSelectedValue(suite, true);
        }
    }

    private void deleteSuite() {
        TestSuite selected = suiteList.getSelectedValue();
        if (selected != null) {
            project.getTestSuites().remove(selected);
            refreshList();
            showPlaceholder();
        }
    }

    private void refreshList() {
        listModel.clear();
        project.getTestSuites().forEach(listModel::addElement);
    }

    private void showPlaceholder() {
        editorContainer.removeAll();
        JLabel label = new JLabel("Select or create a test suite", SwingConstants.CENTER);
        label.setForeground(Color.GRAY);
        editorContainer.add(label, BorderLayout.CENTER);
        editorContainer.revalidate();
        editorContainer.repaint();
    }

    private void showEditor(TestSuite suite) {
        editorContainer.removeAll();
        if (suite != null) {
            editorContainer.add(new TestSuiteEditorPanel(suite, project.getFieldLibrary(), registry), BorderLayout.CENTER);
        } else {
            showPlaceholder();
        }
        editorContainer.revalidate();
        editorContainer.repaint();
    }
}
