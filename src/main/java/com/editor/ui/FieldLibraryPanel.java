package com.editor.ui;

import com.editor.model.Field;
import com.editor.model.FieldLibrary;
import com.editor.model.Group;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class FieldLibraryPanel extends JPanel {
    private final FieldLibrary library;
    private final JPanel contentPanel;

    public FieldLibraryPanel(FieldLibrary library) {
        this.library = library;
        setLayout(new BorderLayout());

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addGroupBtn = new JButton("+ Group");
        addGroupBtn.addActionListener(e -> addGroup());
        toolbar.add(addGroupBtn);
        add(toolbar, BorderLayout.NORTH);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);

        refresh();
    }

    private void addGroup() {
        String name = JOptionPane.showInputDialog(this, "Group name:", "New Group", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.isBlank()) {
            library.getGroups().add(new Group(name.trim()));
            refresh();
        }
    }

    void refresh() {
        contentPanel.removeAll();
        for (Group group : library.getGroups()) {
            contentPanel.add(buildGroupPanel(group));
            contentPanel.add(Box.createVerticalStrut(8));
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel buildGroupPanel(Group group) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), group.getName(),
                TitledBorder.LEFT, TitledBorder.TOP));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JPanel groupBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        JButton renameBtn = new JButton("Rename");
        renameBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "New name:", group.getName());
            if (name != null && !name.isBlank()) {
                group.setName(name.trim());
                refresh();
            }
        });
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> {
            library.getGroups().remove(group);
            refresh();
        });
        JButton addFieldBtn = new JButton("+ Field");
        addFieldBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Field name:");
            if (name != null && !name.isBlank()) {
                group.getFields().add(new Field(name.trim()));
                refresh();
            }
        });
        groupBar.add(renameBtn);
        groupBar.add(deleteBtn);
        groupBar.add(addFieldBtn);

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBorder(new EmptyBorder(0, 12, 4, 4));
        for (Field field : group.getFields()) {
            fieldsPanel.add(buildFieldPanel(group, field));
            fieldsPanel.add(Box.createVerticalStrut(4));
        }

        JPanel inner = new JPanel(new BorderLayout());
        inner.add(groupBar, BorderLayout.NORTH);
        inner.add(fieldsPanel, BorderLayout.CENTER);
        panel.add(inner, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildFieldPanel(Group group, Field field) {
        JPanel panel = new JPanel(new BorderLayout(4, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(4, 8, 4, 8)));

        JLabel nameLabel = new JLabel(field.getName());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
        nameLabel.setPreferredSize(new Dimension(120, 20));

        JPanel valuesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        for (String val : field.getAllowedValues()) {
            JPanel chip = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
            chip.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 200)));
            chip.setBackground(new Color(230, 230, 250));
            chip.setOpaque(true);
            JLabel chipLabel = new JLabel(val);
            JButton removeVal = new JButton("×");
            removeVal.setFont(removeVal.getFont().deriveFont(10f));
            removeVal.setMargin(new Insets(0, 2, 0, 2));
            removeVal.setBorderPainted(false);
            removeVal.setContentAreaFilled(false);
            removeVal.addActionListener(e -> {
                field.getAllowedValues().remove(val);
                refresh();
            });
            chip.add(chipLabel);
            chip.add(removeVal);
            valuesPanel.add(chip);
        }
        JButton addValBtn = new JButton("+ Value");
        addValBtn.setFont(addValBtn.getFont().deriveFont(11f));
        addValBtn.addActionListener(e -> {
            String val = JOptionPane.showInputDialog(this, "Value:");
            if (val != null && !val.isBlank()) {
                field.getAllowedValues().add(val.trim());
                refresh();
            }
        });
        valuesPanel.add(addValBtn);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        JButton renameBtn = new JButton("✎");
        renameBtn.setToolTipText("Rename field");
        renameBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "New name:", field.getName());
            if (name != null && !name.isBlank()) {
                field.setName(name.trim());
                refresh();
            }
        });
        JButton deleteBtn = new JButton("✗");
        deleteBtn.setToolTipText("Delete field");
        deleteBtn.addActionListener(e -> {
            group.getFields().remove(field);
            refresh();
        });
        actionsPanel.add(renameBtn);
        actionsPanel.add(deleteBtn);

        panel.add(nameLabel, BorderLayout.WEST);
        panel.add(valuesPanel, BorderLayout.CENTER);
        panel.add(actionsPanel, BorderLayout.EAST);
        return panel;
    }
}
