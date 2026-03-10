package com.editor.export;

import com.editor.model.ConditionColumn;
import com.editor.model.Project;
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
    public String export(TestSuite suite, Project project) {
        List<ConditionColumn> conditions = suite.getConditions();
        List<ResultColumn> results = suite.getResults();
        List<TestCase> cases = suite.getCases();

        StringBuilder sb = new StringBuilder();
        sb.append("""
            <!DOCTYPE html>
            <html>
            <head>
            <meta charset='UTF-8'>
            """);
        sb.append("<title>").append(escape(suite.getName())).append("</title>\n");
        sb.append("""
            <style>
                body { font-family: monospace; padding: 24px; background: #fff; color: #222; }
                h2 { margin-bottom: 16px; font-family: sans-serif; }
                table { border-collapse: collapse; }
                td, th { border: 1px solid #ccc; padding: 6px 12px; text-align: left; white-space: nowrap; }
                .section  { background: #2c3e50; color: #fff; font-weight: bold; text-transform: uppercase; letter-spacing: 1px; }
                .group    { background: #d8e4f0; font-weight: bold; }
                .field    { background: #f0f4f8; font-style: italic; }
                .value    { padding-left: 24px; }
                .hit      { text-align: center; color: #1a7a1a; font-weight: bold; font-size: 16px; }
                .miss     { text-align: center; color: #c0392b; font-size: 14px; }
                .dontcare { text-align: center; color: #aaa; }
                .result   { background: #fffbe6; }
                .case-hdr { background: #f4f4f4; text-align: center; font-weight: bold; }
                .empty    { color: #bbb; text-align: center; }
            </style>
            </head>
            <body>
            """);
        sb.append("<h2>").append(escape(suite.getName())).append("</h2>\n");
        sb.append("<table>\n");

        // Case header row
        sb.append("<tr><th></th>");
        for (int i = 0; i < cases.size(); i++) {
            sb.append("<th class='case-hdr'>Case ").append(i + 1).append("</th>");
        }
        sb.append("</tr>\n");

        // CONDITIONS section
        sb.append("<tr><td class='section' colspan='").append(cases.size() + 1).append("'>Conditions</td></tr>\n");

        String lastGroup = null;
        for (ConditionColumn cc : conditions) {
            String groupName = cc.getGroup().getName();
            if (!groupName.equals(lastGroup)) {
                sb.append("<tr><td class='group' colspan='").append(cases.size() + 1).append("'>")
                  .append("&#9658; ").append(escape(groupName)).append("</td></tr>\n");
                lastGroup = groupName;
            }
            // Field label row
            sb.append("<tr><td class='field'>&nbsp;&nbsp;").append(escape(cc.getField().getName())).append("</td>");
            for (int i = 0; i < cases.size(); i++) sb.append("<td></td>");
            sb.append("</tr>\n");

            // One row per allowed value
            for (String val : cc.getField().getAllowedValues()) {
                sb.append("<tr><td class='value'>&nbsp;&nbsp;&nbsp;&nbsp;").append(escape(val)).append("</td>");
                for (TestCase tc : cases) {
                    String selected = tc.getConditionValues().getOrDefault(cc, "-");
                    if ("-".equals(selected)) {
                        sb.append("<td class='dontcare'>-</td>");
                    } else if (val.equals(selected)) {
                        sb.append("<td class='hit'>&#9679;</td>");
                    } else {
                        sb.append("<td class='miss'>&#10007;</td>");
                    }
                }
                sb.append("</tr>\n");
            }
        }

        // RESULTS section
        sb.append("<tr><td class='section' colspan='").append(cases.size() + 1).append("'>Results</td></tr>\n");

        lastGroup = null;
        for (ResultColumn rc : results) {
            String groupName = rc.getGroup().getName();
            if (!groupName.equals(lastGroup)) {
                sb.append("<tr><td class='group' colspan='").append(cases.size() + 1).append("'>")
                  .append("&#9658; ").append(escape(groupName)).append("</td></tr>\n");
                lastGroup = groupName;
            }
            sb.append("<tr><td class='field result'>&nbsp;&nbsp;").append(escape(rc.getName())).append("</td>");
            for (TestCase tc : cases) {
                String val = tc.getResultValues().getOrDefault(rc, "");
                if (val.isEmpty()) {
                    sb.append("<td class='empty result'>-</td>");
                } else {
                    sb.append("<td class='result'>").append(escape(val)).append("</td>");
                }
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
