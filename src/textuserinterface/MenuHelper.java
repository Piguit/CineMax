package textuserinterface;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import utility.OutputPrinter;

/**
 * Classe di supporto per la lettura e validazione dell'input da console.
 * Ogni metodo implementa un ciclo di retry che si ripete finché l'input
 * non soddisfa i requisiti, stampando un messaggio di errore ad ogni
 * tentativo fallito. Il parametro {@code mandatory} specifica se l'input è
 * obbligatorio o meno.
 */
public class MenuHelper {
    /** 
     * Scanner condiviso da tutti i metodi di lettura.
     * */
    private static final Scanner scanner = new Scanner(System.in);
    /** Formato atteso per i campi data: giorno/mese/anno. */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    /** Formato atteso per i campi data e ora: giorno/mese/anno ora:minuti. */
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    /** Istanza di {@link OutputPrinter} usata per stampare prompt e messaggi di errore. */
    private OutputPrinter op;

    /**
     * Costruisce un {@code MenuHelper} con l'output printer specificato.
     * @param op printer usato per stampare prompt e messaggi di errore
     */
    public MenuHelper(OutputPrinter op) {
        this.op = op;
    }

    /**
     * Legge un intero nell'intervallo [{@code min}, {@code max}] (estremi inclusi).
     * Il campo è sempre obbligatorio.
     * @param msg prompt da mostrare all'utente
     * @param min valore minimo accettato
     * @param max valore massimo accettato
     * @return intero letto, compreso tra {@code min} e {@code max}
     */
    public int readInt(String msg, int min, int max) {
        int val;
        while (true) {
            op.print(msg);
            try {
                val = Integer.parseInt(scanner.nextLine());
                if (val < min || val > max) {
                    op.println("Inserire un numero compreso tra " + min + " e " + max + ".");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                op.println("Inserire un numero intero.");
            }
        }
    }

    /**
     * Legge un {@code Byte} strettamente positivo (> 0).
     * @param msg prompt da mostrare all'utente
     * @param mandatory se {@code true}, input vuoto viene rifiutato;
     *                  se {@code false}, input vuoto restituisce {@code null}
     * @return valore letto oppure {@code null} se non obbligatorio e input vuoto
     */
    public Byte readPositiveByte(String msg, boolean mandatory) {
        while (true) {
            op.print(msg);
            try {
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    if (!mandatory)
                        return null;
                    else {
                        op.println("Campo obbligatorio.");
                        continue;
                    }
                }
                byte result = Byte.parseByte(input);
                if (result <= 0)
                    throw new NumberFormatException();
                return result;
            } catch (NumberFormatException e) {
                op.println("Inserire un numero intero positivo.");
            }
        }
    }

    /**
     * Legge uno {@code Short} strettamente positivo (> 0).
     * @param msg prompt da mostrare all'utente
     * @param mandatory se {@code true}, input vuoto viene rifiutato;
     *                  se {@code false}, input vuoto restituisce {@code null}
     * @return valore letto oppure {@code null} se non obbligatorio e input vuoto
     */
    public Short readPositiveShort(String msg, boolean mandatory) {
        while (true) {
            op.print(msg);
            try {
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    if (!mandatory)
                        return null;
                    else {
                        op.println("Campo obbligatorio.");
                        continue;
                    }
                }
                short result = Short.parseShort(input);
                if (result <= 0)
                    throw new NumberFormatException();
                return result;
            } catch (NumberFormatException e) {
                op.println("Inserire un numero intero positivo.");
            }
        }
    }

    /**
     * Legge un {@code Long} non negativo.
     * @param msg prompt da mostrare all'utente
     * @param mandatory se {@code true}, input vuoto viene rifiutato;
     *                  se {@code false}, input vuoto restituisce {@code null}
     * @return valore letto oppure {@code null} se non obbligatorio e input vuoto
     */
    public Long readLong(String msg, boolean mandatory) {
        while (true) {
            op.print(msg);
            try {
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    if (!mandatory)
                        return null;
                    else {
                        op.println("Campo obbligatorio.");
                        continue;
                    }
                }
                long result = Long.parseLong(input);
                if (result < 0)
                    throw new NumberFormatException();
                return result;
            } catch (NumberFormatException e) {
                op.println("Inserire un numero intero non negativo.");
            }
        }
    }

    /**
     * Legge un {@code Float} non negativo.
     * @param msg prompt da mostrare all'utente
     * @param mandatory se {@code true}, input vuoto viene rifiutato;
     *                  se {@code false}, input vuoto restituisce {@code null}
     * @return valore letto oppure {@code null} se non obbligatorio e input vuoto
     */
    public Float readFloat(String msg, boolean mandatory) {
        while (true) {
            op.print(msg);
            try {
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    if (!mandatory)
                        return null;
                    else {
                        op.println("Campo obbligatorio.");
                        continue;
                    }
                }
                float result = Float.parseFloat(input);
                if (result < 0)
                    throw new NumberFormatException();
                return result;
            } catch (NumberFormatException e) {
                op.println("Inserire un numero non negativo.");
            }
        }
    }

    /**
     * Legge una stringa non contenente i caratteri {@code ~} e {@code |},
     * riservati rispettivamente al separatore di campo e al formato
     * {@code toString()} delle entità model. L'input subisce trim.
     * @param msg prompt da mostrare all'utente
     * @param mandatory se {@code true}, input vuoto viene rifiutato;
     *                  se {@code false}, input vuoto restituisce {@code null}
     * @return stringa letta oppure {@code null} se non obbligatorio e input vuoto
     */
    public String readString(String msg, boolean mandatory) {
        while (true) {
            try {
                op.print(msg);
                String input = scanner.nextLine().trim();
                if (input.contains("~") || input.contains("|"))
                    throw new IllegalArgumentException();
                if (!mandatory || !input.isEmpty())
                    return (input.isEmpty()) ? null : input;
                op.println("Campo obbligatorio.");
            } catch (IllegalArgumentException e) {
                op.println("L'input non puo' contenere i caratteri ~ e |.");
            }
        }
    }

    /**
     * Legge una data nel formato {@code dd/MM/yyyy}.
     * @param msg prompt da mostrare all'utente
     * @param mandatory se {@code true}, input vuoto viene rifiutato;
     *                  se {@code false}, input vuoto restituisce {@code null}
     * @return data letta oppure {@code null} se non obbligatorio e input vuoto
     */
    public LocalDate readDate(String msg, boolean mandatory) {
        while (true) {
            op.print(msg);
            try {
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    if (!mandatory)
                        return null;
                    else {
                        op.println("Campo obbligatorio.");
                        continue;
                    }
                }
                return LocalDate.parse(input, DATE_FORMAT);
            } catch (Exception e) {
                op.println("Data non valida.");
            }
        }
    }

    /**
     * Legge una data e ora nel formato {@code dd/MM/yyyy HH:mm}.
     * @param msg prompt da mostrare all'utente
     * @param mandatory se {@code true}, input vuoto viene rifiutato;
     *                  se {@code false}, input vuoto restituisce {@code null}
     * @return data e ora lette oppure {@code null} se non obbligatorio e input vuoto
     */
    public LocalDateTime readDateAndTime(String msg, boolean mandatory) {
        while (true) {
            op.print(msg);
            try {
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    if (!mandatory)
                        return null;
                    else {
                        op.println("Campo obbligatorio.");
                        continue;
                    }
                }
                return LocalDateTime.parse(input, DATE_TIME_FORMAT);
            } catch (Exception e) {
                op.println("Data non valida.");
            }
        }
    }
}
