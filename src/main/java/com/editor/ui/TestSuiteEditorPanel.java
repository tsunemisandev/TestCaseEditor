package com.editor.ui;

import com.editor.export.Exporter;
import com.editor.export.ExporterRegistry;
import com.editor.model.ConditionColumn;
import com.editor.model.Field;
import com.editor.model.FieldLibrary;
import com.editor.model.Group;
import com.editor.model.ResultColumn;
import com.editor.model.TestCase;
import com.editor.model.TestSuite;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class TestSuiteEditorPanel extends JPanel {
    private final TestSuite suite;
    private final FieldLibrary library;
    private final ExporterRegistry registry;

    private final JPanel conditionsPanel;
    private final JPanel resultsPanel;
    private final JPanel gridContainer;
    private TestCaseGrid grid;

    public TestSuiteEditorPanel(TestSuite suite, FieldLibrary library, ExporterRegistry registry) {
        this.suite = suite;
        this.library = library;
        this.registry = registry;
        setLayout(new BorderLayout(0, 4));
        setBorder(new EmptyBorder(4, 4, 4, 4));

        // Top section: suite name
        JLabel nameLabel = new JLabel(suite.getName());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 14f));
        nameLabel.setBorder(new EmptyBorder(0, 0, 4, 0));

        // Config: conditions + results side by side
        conditionsPanel = new JPanel();
        conditionsPanel.setLayout(new BoxLayout(conditionsPanel, BoxLayout.Y_AXIS));
        conditionsPanel.setBorder(BorderFactory.createTitledBorder("Conditions"));

        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Results"));

        JPanel configPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        configPanel.add(new JScrollPane(conditionsPanel));
        configPanel.add(new JScrollPane(resultsPanel));

        JPanel topSection = new JPanel(new BorderLayout(0, 4));
        topSection.add(nameLabel, BorderLayout.NORTH);
        topSection.add(configPanel, BorderLayout.CENTER);
        topSection.setPreferredSize(new Dimension(0, 180));
        add(topSection, BorderLayout.NORTH);

        // Bottom section: grid
        gridContainer = new JPanel(new BorderLayout());
        gridContainer.setBorder(BorderFactory.createTitledBorder("Test Cases"));

        JPanel gridToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        JButton addCaseBtn = new JButton("+ Case");
        addCaseBtn.addActionListener(e -> addCase());
        gridToolbar.add(addCaseBtn);

        for (Exporter exporter : registry.all()) {
            JButton exportBtn = new JButton("Export " + exporter.displayName());
            exportBtn.addActionListener(e -> export(exporter));
            gridToolbar.add(exportBtn);
        }

        gridContainer.add(gridToolbar, BorderLayout.NORTH);
        add(gridContainer, BorderLayout.CENTER);

        refreshConditions();
        refreshResults();
        refreshGrid();
    }

    private void refreshConditions() {
        conditionsPanel.removeAll();

        JPanel addBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        JButton addBtn = new JButton("+ Add Field");
        addBtn.addActionListener(e -> addCondition());
        addBar.add(addBtn);
        conditionsPanel.add(addBar);

        for (ConditionColumn col : suite.getConditions()) {
            JPanel row = new JPanel(new BorderLayout());
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            row.add(new JLabel("  " + col), BorderLayout.CENTER);
            JButton removeBtn = smallRemoveButton();
            removeBtn.addActionListener(e -> {
                suite.getConditions().remove(col);
                refreshConditions();
                refreshGrid();
            });
            row.add(removeBtn, BorderLayout.EAST);
            conditionsPanel.add(row);
        }
        conditionsPanel.revalidate();
        conditionsPanel.repaint();
    }

    private void refreshResults() {
        resultsPanel.removeAll();

        JPanel addBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        JButton addBtn = new JButton("+ Add Result");
        addBtn.addActionListener(e -> addResult());
        addBar.add(addBtn);
        resultsPanel.add(addBar);

        for (ResultColumn col : suite.getResults()) {
            JPanel row = new JPanel(new BorderLayout());
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            row.add(new JLabel("  " + col.getName()), BorderLayout.CENTER);
            JButton removeBtn = smallRemoveButton();
            removeBtn.addActionListener(e -> {
                suite.getResults().remove(col);
                refreshResults();
                refreshGrid();
            });
            row.add(removeBtn, BorderLayout.EAST);
            resultsPanel.add(row);
        }
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private void refreshGrid() {
        if (grid != null) gridContainer.remove(grid);
        grid = new TestCaseGrid(suite);
        gridContainer.add(grid, BorderLayout.CENTER);
        gridContainer.revalidate();
        gridContainer.repaint();
    }

    private void addCondition() {
        List<ConditionColumn> available = new ArrayList<>();
        for (Group group : library.getGroups()) {
            for (Field field : group.getFields()) {
                available.add(new ConditionColumn(group, field));
            }
        }
        if (available.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No fields defined in the Field Library.",
                    "No Fields", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        ConditionColumn[] options = available.toArray(new ConditionColumn[0]);
        ConditionColumn selected = (ConditionColumn) JOptionPane.showInputDialog(
                this, "Select field:", "Add Condition",
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (selected != null) {
            suite.getConditions().add(selected);
            refreshConditions();
            refreshGrid();
        }
    }

    private void addResult() {
        String name = JOptionPane.showInputDialog(this, "Result name:", "New Result", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.isBlank()) {
            suite.getResults().add(new ResultColumn(name.trim()));
            refreshResults();
            refreshGrid();
        }
    }

    private void addCase() {
        TestCase tc = new TestCase();
        for (ConditionColumn col : suite.getConditions()) {
            tc.getConditionValues().put(col, "-");
        }
        suite.getCases().add(tc);
        grid.refresh();
    }

    private void export(Exporter exporter) {
        String content = exporter.export(suite, library);
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(suite.getName() + "." + exporter.fileExtension()));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Files.writeString(chooser.getSelectedFile().toPath(), content, StandardCharsets.UTF_8);
                JOptionPane.showMessageDialog(this, "Exported to " + chooser.getSelectedFile().getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton smallRemoveButton() {
        JButton btn = new JButton("✗");
        btn.setFont(btn.getFont().deriveFont(10f));
        btn.setMargin(new Insets(0, 4, 0, 4));
        return btn;
    }
}
