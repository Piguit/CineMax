package TUInterface;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class MenuHelper {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
                return Byte.parseByte(input);
            } catch (NumberFormatException e) {
                System.out.println("Inserire un numero.");
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
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Inserire un numero.");
            }
        }
    }

    public static Double readDouble(String msg, boolean mandatory) {
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
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Inserire un numero.");
            }
        }
    }

    public static String readString(String msg, boolean mandatory) {
        while (true) {
            System.out.print(msg);
            String input = scanner.nextLine().trim();
            if (!mandatory || !input.isEmpty())
                return (input.isEmpty()) ? null : input;
            System.out.println("Campo obbligatorio.");
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

    public static <T> void printList(List<T> list) {
        for (T o: list)
            System.out.println(o);
    }
}
