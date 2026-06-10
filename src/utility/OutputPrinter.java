package utility;

import java.util.List;

public class OutputPrinter {
    private final String margin;
    private final String marked;
    private final int rowLength;

    public OutputPrinter(String margin, String marked, int rowLength) {
        this.margin = margin;
        this.marked = marked;
        this.rowLength = rowLength;
    }

    private String getStringWithMargin(String text, String mEnd) {
        if (text == null || text.isEmpty())
            return "";

        String pre1 = mEnd;
        String pre2 = margin;

        StringBuilder sb = new StringBuilder();
        String[] lines = text.split("\\n", -1);
        int length = lines.length;
        for (int i = 0; i < length; i++) {
            String line = lines[i];
            if (line.isEmpty()) {
                if (i != length - 1)
                    sb.append(pre2 + "\n");
                continue;
            }

            line = line.replaceAll(" {2,}", " ");

            int start = 0;
            while (line.length() > start + rowLength) {
                int end = start + rowLength;
                int lastSpace = line.lastIndexOf(' ', end);
                if (lastSpace > start) {
                    sb.append(pre1).append(line, start, lastSpace).append("\n");
                    start = lastSpace + 1;
                } else {
                    sb.append(pre1).append(line, start, end).append("\n");
                    start = end;
                }
                pre1 = pre2;
            }
            if (start < line.length()) {
                sb.append(pre1).append(line, start, line.length());
                pre1 = pre2;
                if (i != length - 1)
                    sb.append("\n");
            } else if (i == length - 1)
                sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public void print(String text) {
        System.out.print(getStringWithMargin(text, margin));
    }

    public void println(String text) {
        System.out.println(getStringWithMargin(text, margin));
    }

    public void printlnMarked(String text) {
        System.out.println(getStringWithMargin(text, marked));
    }

    public void printlnMarkedByChunk(List<String> strings) {
        StringBuilder bd = new StringBuilder();
        for (String str : strings)
            bd.append(getStringWithMargin(str, marked)).append("\n");
        System.out.print(bd);
    }
}
