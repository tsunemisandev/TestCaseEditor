package com.editor.export;

import com.editor.model.ConditionColumn;
import com.editor.model.FieldLibrary;
import com.editor.model.ResultColumn;
import com.editor.model.TestCase;
import com.editor.model.TestSuite;

import java.util.List;

public class HtmlExporter implements Exporter {

    @Override
    public String id() { return "html"; }

    @Override
    public String displayName() { return "HTML"; }

    @Override
    public String fileExtension() { return "html"; }

    @Override
    public String export(TestSuite suite, FieldLibrary library) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n<html>\n<head>\n");
        sb.append("<meta charset='UTF-8'>\n");
        sb.append("<title>").append(escape(suite.getName())).append("</title>\n");
        sb.append("""
            <style>
                body { font-family: sans-serif; padding: 24px; background: #fff; color: #222; }
                h2 { margin-bottom: 16px; }
                table { border-collapse: collapse; }
                th, td { border: 1px solid #ccc; padding: 8px 14px; white-space: nowrap; }
                th { background: #f4f4f4; }
                .group-header { background: #d8e4f0; text-align: center; font-weight: bold; }
                .case-label { font-weight: bold; color: #555; text-align: center; }
                .empty { color: #bbb; text-align: center; }
            </style>
            """);
        sb.append("</head>\n<body>\n");
        sb.append("<h2>").append(escape(suite.getName())).append("</h2>\n");
        sb.append("<table>\n");

        List<ConditionColumn> conditions = suite.getConditions();
        List<ResultColumn> results = suite.getResults();
        List<TestCase> cases = suite.getCases();

        // Row 1: group headers + "Results" spanning all result columns
        sb.append("<tr><th></th>");
        for (ConditionColumn col : conditions) {
            sb.append("<th class='group-header'>").append(escape(col.getGroup().getName())).append("</th>");
        }
        if (!results.isEmpty()) {
            sb.append("<th class='group-header' colspan='").append(results.size()).append("'>Results</th>");
        }
        sb.append("</tr>\n");

        // Row 2: field names + result column names
        sb.append("<tr><th></th>");
        for (ConditionColumn col : conditions) {
            sb.append("<th>").append(escape(col.getField().getName())).append("</th>");
        }
        for (ResultColumn col : results) {
            sb.append("<th>").append(escape(col.getName())).append("</th>");
        }
        sb.append("</tr>\n");

        // Data rows
        int caseNum = 1;
        for (TestCase tc : cases) {
            sb.append("<tr>");
            sb.append("<td class='case-label'>Case ").append(caseNum++).append("</td>");
            for (ConditionColumn col : conditions) {
                String val = tc.getConditionValues().getOrDefault(col, "-");
                boolean empty = val.isEmpty() || "-".equals(val);
                sb.append(empty ? "<td class='empty'>-</td>" : "<td>" + escape(val) + "</td>");
            }
            for (ResultColumn col : results) {
                String val = tc.getResultValues().getOrDefault(col, "");
                boolean empty = val.isEmpty();
                sb.append(empty ? "<td class='empty'>-</td>" : "<td>" + escape(val) + "</td>");
            }
            sb.append("</tr>\n");
        }

        sb.append("</table>\n</body>\n</html>");
        return sb.toString();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
