package utility;

import java.util.List;

/**
 * Classe preposta alla stampa custom di stringhe.
 * La formattazione eseguita dalle istanze di questa classe produce un margine a sinistra
 * delle varie righe da stampare; una riga troppo lunga viene divisa su più righe.
 * @author Piergiorgio Tomaciello 761013
 * CO
 */
public class OutputPrinter {
    private final String margin;
    private final String marked;
    private final int rowLength;

    /**
     * Costruttore che accetta un margine, un margine speciale e la lunghezza massima
     * delle righe da stampare a video.
     * @param margin margine che precede ogni riga di un testo da stampare, salvo il caso gestito
     *               attraverso {@link #marked}
     * @param marked margine anteposto soltanto alla prima riga non vuota di una stringa
     * @param rowLength lunghezza massima delle righe da stampare
     */
    public OutputPrinter(String margin, String marked, int rowLength) {
        this.margin = margin;
        this.marked = marked;
        this.rowLength = rowLength;
    }

    /**
     * Collassa gli spazi multipli e formatta la stringa attraverso l'aggiunta di margini custom
     * alla sua sinistra: viene divisa in tante righe quanti sono i caratteri linefeed e ad ogni riga viene
     * anteposto {@link #margin}; solo la prima riga non vuota viene trattata diversamente,
     * con l'anteposizione di un margine costituito dal parametro attuale in chiamata al metodo.
     * Una riga troppo lunga viene spezzata in sottostringhe in corrispondenza di uno spazio;
     * se non presente, la divisione è eseguita dopo {@link #rowLength} caratteri dall'inizio.
     * In caso di termine della stringa con un carattere linefeed, questo viene ignorato.
     * @param text stringa da formattare
     * @param mEnd margine speciale da anteporre alla prima riga non vuota della stringa
     * @return una stringa formattata a sinistra con {@link #margin} e {@code mEnd}, altrimenti
     *         una stringa vuota se quella in input è {@code null} o vuota
     */
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

    /**
     * Stampa la stringa in input; ogni riga da cui è composta viene preceduta da {@link #margin}.
     * @param text stringa da stampare
     */
    public void print(String text) {
        System.out.print(getStringWithMargin(text, margin));
    }

    /**
     * Stampa la stringa in input; ogni riga da cui è composta viene preceduta da {@link #margin}.
     * Aggiunge un linefeed finale.
     * @param text stringa da stampare
     */
    public void println(String text) {
        System.out.println(getStringWithMargin(text, margin));
    }

    /**
     * Stampa la stringa in input; la prima riga non vuota viene preceduta da {@link #marked}, mentre le altre,
     * se presenti, vengono precedute da {@link #margin}. Aggiunge un linefeed finale.
     * @param text stringa da stampare
     */
    public void printlnMarked(String text) {
        System.out.println(getStringWithMargin(text, marked));
    }

    /**
     * Versione iterativa di {@link #printlnMarked(String)}.
     * @param strings lista di stringhe da stampare.
     */
    public void printlnMarkedByChunk(List<String> strings) {
        StringBuilder bd = new StringBuilder();
        for (String str : strings)
            bd.append(getStringWithMargin(str, marked)).append("\n");
        System.out.print(bd);
    }
}
