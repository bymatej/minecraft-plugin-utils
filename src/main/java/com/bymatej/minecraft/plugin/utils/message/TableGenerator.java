package com.bymatej.minecraft.plugin.utils.message;

import java.util.ArrayList;
import java.util.List;

public class TableGenerator {

    public static final String DASHES_LINE_DELIMITER = "--------------------"; // 20 dashes

    private final Alignment[] alignments;

    private final List<Row> table = new ArrayList<>();

    private final int columns;

    public TableGenerator(Alignment... alignments) {
        this.columns = alignments.length;
        this.alignments = alignments;
    }

    public List<String> generate() {
        Integer[] columWidths = new Integer[columns];

        for (Row r : table) {
            for (int i = 0; i < columns; i++) {
                if (columWidths[i] == null || r.texts.get(i).length() > columWidths[i]) {
                    columWidths[i] = r.texts.get(i).length();
                }
            }
        }

        List<String> lines = new ArrayList<>();

        for (Row r : table) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < columns; i++) {
                Alignment agn = alignments[i];
                String text = r.texts.get(i);
                String spaces = spaces(columWidths[i] - text.length());

                if (agn == Alignment.LEFT) {
                    sb.append(text).append(spaces);
                }
                if (agn == Alignment.RIGHT) {
                    sb.append(spaces).append(text);
                }
                if (agn == Alignment.CENTER) {
                    int left = spaces.length() / 2;
                    int right = spaces.length() - left;

                    sb.append(spaces(left)).append(text).append(spaces(right));
                }

                if (i < columns - 1) {
                    String delimiter = " | ";
                    sb.append(delimiter);
                }
            }

            lines.add(sb.toString());
        }
        return lines;
    }

    public void addRow(String... texts) {
        if (texts.length > columns) {
            throw new IllegalArgumentException("Too big for the table.");
        }

        Row r = new Row(texts);

        for (int i = 0; i < columns; i++) {
            if (i >= texts.length) {
                r.texts.add("");
            }
        }

        table.add(r);
    }

    private String spaces(int length) {
        return " ".repeat(Math.max(0, length));
    }

    private static class Row {

        private List<String> texts = new ArrayList<>();

        public Row(String... texts) {
            for (String text : texts) {
                this.texts.add(text == null ? "" : text);
            }
        }

        public List<String> getTexts() {
            return texts;
        }

        public void setTexts(List<String> texts) {
            this.texts = texts;
        }
    }

    public enum Alignment {
        CENTER,
        LEFT,
        RIGHT
    }
}
