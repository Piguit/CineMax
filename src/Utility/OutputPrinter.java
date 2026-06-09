package Utility;

public class OutputPrinter {
    private final String margin;
    private final String marked;
    private final int rowLength;

    public OutputPrinter(String margin, String marked, int rowLength) {
        this.margin = margin;
        this.marked = marked;
        this.rowLength = rowLength;
    }

    private void printWithMargin(String text, String mEnd) {
        if (text == null || text.isEmpty())
            return;

        String pre1 = mEnd;
        String pre2 = margin;

        String[] lines = text.split("\\n", -1);
        int length = lines.length;
        for (int i = 0; i < length; i++) {
            String line = lines[i];
            if (line.isEmpty()) {
                if (i != length - 1)
                    System.out.println();
                continue;
            }

            line = line.replaceAll(" {2,}", " ");

            StringBuilder sb = new StringBuilder();
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
            }

            if (i != length - 1)
                sb.append("\n");

            System.out.print(sb);
        }
    }

    public void print(String text) {
        printWithMargin(text, margin);
    }

    public void println(String text) {
        printWithMargin(text, margin);
        System.out.println();
    }

    public void printlnMarked(String text) {
        printWithMargin(text, marked);
        System.out.println();
    }
}
