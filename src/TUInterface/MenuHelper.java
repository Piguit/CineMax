package TUInterface;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class MenuHelper {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static int readInt(String msg, int min, int max) {
        int val;
        while (true) {
            System.out.print(msg);
            try {
                val = Integer.parseInt(scanner.nextLine());
                if (val < min || val > max) {
                    System.out.println("Inserire un numero compreso tra " + min + " e " + max + ".");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Inserire un numero intero.");
            }
        }
    }

    // da deprecare?
    public static Byte readByte(String msg, boolean mandatory) {
        while (true) {
            System.out.print(msg);
            try {
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    if (!mandatory)
                        return null;
                    else {
                        System.out.println("Campo obbligatorio.");
                        continue;
                    }
                }
                byte result = Byte.parseByte(input);
                if (result < 0)
                    throw new NumberFormatException();
                return result;
            } catch (NumberFormatException e) {
                System.out.println("Inserire un numero intero non negativo.");
            }
        }
    }

    public static Short readShort(String msg, boolean mandatory) {
        while (true) {
            System.out.print(msg);
            try {
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    if (!mandatory)
                        return null;
                    else {
                        System.out.println("Campo obbligatorio.");
                        continue;
                    }
                }
                short result = Short.parseShort(input);
                if (result < 0)
                    throw new NumberFormatException();
                return result;
            } catch (NumberFormatException e) {
                System.out.println("Inserire un numero intero non negativo.");
            }
        }
    }

    public static Long readLong(String msg, boolean mandatory) {
        while (true) {
            System.out.print(msg);
            try {
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    if (!mandatory)
                        return null;
                    else {
                        System.out.println("Campo obbligatorio.");
                        continue;
                    }
                }
                long result = Long.parseLong(input);
                if (result < 0)
                    throw new NumberFormatException();
                return result;
            } catch (NumberFormatException e) {
                System.out.println("Inserire un numero intero non negativo.");
            }
        }
    }

    public static Float readFloat(String msg, boolean mandatory) {
        while (true) {
            System.out.print(msg);
            try {
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    if (!mandatory)
                        return null;
                    else {
                        System.out.println("Campo obbligatorio.");
                        continue;
                    }
                }
                float result = Float.parseFloat(input);
                if (result < 0)
                    throw new NumberFormatException();
                return result;
            } catch (NumberFormatException e) {
                System.out.println("Inserire un numero non negativo.");
            }
        }
    }

    public static String readString(String msg, boolean mandatory) {
        while (true) {
            try {
                System.out.print(msg);
                String input = scanner.nextLine().trim();
                if (input.contains("~") || input.contains("|"))
                    throw new IllegalArgumentException();
                if (!mandatory || !input.isEmpty())
                    return (input.isBlank()) ? null : input;
                System.out.println("Campo obbligatorio.");
            } catch (IllegalArgumentException e) {
                System.out.println("L'input non puo' contenere i caratteri ~ e |.");
            }
        }
    }

    public static LocalDate readDate(String msg, boolean mandatory) {
        while (true) {
            System.out.print(msg);
            try {
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    if (!mandatory)
                        return null;
                    else {
                        System.out.println("Campo obbligatorio.");
                        continue;
                    }
                }
                return LocalDate.parse(input, DATE_FORMAT);
            } catch (Exception e) {
                System.out.println("Data non valida.");
            }
        }
    }

    public static LocalDateTime readDateAndTime(String msg, boolean mandatory) {
        while (true) {
            System.out.print(msg);
            try {
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    if (!mandatory)
                        return null;
                    else {
                        System.out.println("Campo obbligatorio.");
                        continue;
                    }
                }
                return LocalDateTime.parse(input, DATE_TIME_FORMAT);
            } catch (Exception e) {
                System.out.println("Data non valida.");
            }
        }
    }

    public static <T> void printList(List<T> list, String separator, String margin) {
        System.out.print(separator);
        if (list.isEmpty())
            System.out.println(margin + "Nessun risultato trovato.");
        else
            for (T o: list)
                System.out.println(margin + o);
    }
}
