package com.editor.ui;

import com.editor.model.ConditionColumn;
import com.editor.model.ResultColumn;
import com.editor.model.TestCase;
import com.editor.model.TestSuite;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.util.List;

public class TestCaseGrid extends JScrollPane {
    private final TestSuite suite;
    private final JTable table;
    private final CaseTableModel model;

    public TestCaseGrid(TestSuite suite) {
        this.suite = suite;
        this.model = new CaseTableModel();
        this.table = new JTable(model) {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                return buildEditor(convertColumnIndexToModel(column));
            }
        };
        table.setRowHeight(28);
        table.setGridColor(java.awt.Color.LIGHT_GRAY);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setViewportView(table);
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        resizeColumns();
    }

    private TableCellEditor buildEditor(int col) {
        if (col == 0) return new DefaultCellEditor(new JTextField());
        int condCount = suite.getConditions().size();
        if (col <= condCount) {
            ConditionColumn cc = suite.getConditions().get(col - 1);
            List<String> vals = cc.getField().getAllowedValues();
            String[] options = new String[vals.size() + 1];
            options[0] = "-";
            for (int i = 0; i < vals.size(); i++) options[i + 1] = vals.get(i);
            return new DefaultCellEditor(new JComboBox<>(options));
        }
        int resultIdx = col - 1 - condCount;
        ResultColumn rc = suite.getResults().get(resultIdx);
        if (!rc.getAllowedValues().isEmpty()) {
            return new DefaultCellEditor(new JComboBox<>(rc.getAllowedValues().toArray(new String[0])));
        }
        return new DefaultCellEditor(new JTextField());
    }

    private void resizeColumns() {
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(i == 0 ? 70 : 140);
        }
    }

    void refresh() {
        model.fireTableStructureChanged();
        resizeColumns();
    }

    private class CaseTableModel extends AbstractTableModel {

        @Override
        public int getColumnCount() {
            return 1 + suite.getConditions().size() + suite.getResults().size();
        }

        @Override
        public int getRowCount() {
            return suite.getCases().size();
        }

        @Override
        public String getColumnName(int col) {
            if (col == 0) return "#";
            int condCount = suite.getConditions().size();
            if (col <= condCount) return suite.getConditions().get(col - 1).toString();
            return suite.getResults().get(col - 1 - condCount).getName();
        }

        @Override
        public Object getValueAt(int row, int col) {
            TestCase tc = suite.getCases().get(row);
            if (col == 0) return "Case " + (row + 1);
            int condCount = suite.getConditions().size();
            if (col <= condCount) {
                ConditionColumn cc = suite.getConditions().get(col - 1);
                return tc.getConditionValues().getOrDefault(cc, "-");
            }
            ResultColumn rc = suite.getResults().get(col - 1 - condCount);
            return tc.getResultValues().getOrDefault(rc, "");
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            if (col == 0) return;
            TestCase tc = suite.getCases().get(row);
            int condCount = suite.getConditions().size();
            if (col <= condCount) {
                tc.getConditionValues().put(suite.getConditions().get(col - 1), String.valueOf(value));
            } else {
                tc.getResultValues().put(suite.getResults().get(col - 1 - condCount), String.valueOf(value));
            }
            fireTableCellUpdated(row, col);
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return col > 0;
        }
    }
}
