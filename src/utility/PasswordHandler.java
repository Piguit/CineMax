package utility;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * La classe {@code PasswordHandler} si utilizza per la gestione delle
 * funzioni di hashing e di verifica delle password all'interno del sistema
 * con l'utilizzo dell'algoritmo di hashing PBKDF2WithHmacSHA256.
 * @author Piergiorgio Tomaciello 761013
 * CO
 */
public class PasswordHandler {
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 600000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    /**
     * Esegue la funzione di hashing della password e la restituisce in una forma
     * composta dalle stringhe del salt e della password hashata
     * @param password password ricevuta come argomento
     * @return stringa nel formato 'salt:hash' codificata in Base64
     * @throws NoSuchAlgorithmException se l'algoritmo PBKDF2WithHmacSHA256 non è disponibile
     * @throws InvalidKeySpecException se la chiave specificata non è valida
     */
    public static String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = generateSalt();
        byte[] hash = pbkdf2(password.toCharArray(), salt);
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hashBase64 = Base64.getEncoder().encodeToString(hash);
        return saltBase64 + ":" + hashBase64;
    }

    /**
     * Esegue l'operazione di verifica della password con un controllo sulla
     * stringa hashata e, se questa corrisponde al formato corretto, eseguirà
     * un confronto bit a bit tra le due restituendo true se combaciano.
     * @param password password ricevuta come argomento
     * @param storedSaltAndHash il salt e l'hash salvato
     * @return true se la password corrisponde all'hash salvato, false altrimenti
     * @throws NoSuchAlgorithmException se l'algoritmo PBKDF2WithHmacSHA256 non è disponibile
     * @throws InvalidKeySpecException se la chiave specificata non è valida
     */
    public static boolean verifyPassword(String password, String storedSaltAndHash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = storedSaltAndHash.split(":");
        if (parts.length != 2) {
            return false;
        }
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] storedHash = Base64.getDecoder().decode(parts[1]);
        byte[] newHash = pbkdf2(password.toCharArray(), salt);
        return slowEquals(storedHash, newHash);
    }

    /**
     * Esegue l'operazione di ricavare un hash crittografico dalla password e dal salt
     * utilizzando l'algoritmo di hashing PBKDF2WithHmacSHA256
     * @param password password ricevuta come argomento
     * @param salt 16 byte casuali generati
     * @return array di byte hashato
     * @throws NoSuchAlgorithmException se l'algoritmo PBKDF2WithHmacSHA256 non è disponibile
     * @throws InvalidKeySpecException se la chiave specificata non è valida
     */
    private static byte[] pbkdf2(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        return factory.generateSecret(spec).getEncoded();
    }

    /**
     * Genera un salt casuale di {@link #SALT_LENGTH} byte con
     * l'utilizzo di {@link SecureRandom}
     * @return array di 16 byte casuali
     */
    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Confronta due array di byte lentamente in tempo costante per
     * evitare che il tempo di risposta riveli dati di hashing
     * @param a primo array di byte come argomento
     * @param b secondo array di byte come argomento
     * @return true se i byte ricevuti come argomento sono identici, false altrimenti
     */
    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }
}