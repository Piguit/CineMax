package textuserinterface;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import utility.OutputPrinter;

public class MenuHelper {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private OutputPrinter op;

    public MenuHelper(OutputPrinter op) {
        this.op = op;
    }

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

    public Byte readByte(String msg, boolean mandatory) {
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
                if (result < 0)
                    throw new NumberFormatException();
                return result;
            } catch (NumberFormatException e) {
                op.println("Inserire un numero intero non negativo.");
            }
        }
    }

    public Short readShort(String msg, boolean mandatory) {
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
                if (result < 0)
                    throw new NumberFormatException();
                return result;
            } catch (NumberFormatException e) {
                op.println("Inserire un numero intero non negativo.");
            }
        }
    }

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
