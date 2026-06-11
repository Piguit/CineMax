package model;

/**
 * L'interfaccia {@code Identifiable<E>} definisce il contratto che
 * le classi che la implementano devono rispettare.
 * @param <E> tipo che sarà inserito in base alla classe che lo implementa
 * @author Edo Hodzic 761022
 * @author Piergiorgio Tomaciello 761013
 * @author Tamirat Tucci 756969
 * CO
 */
public interface Identifiable<E> {
    /**
     * Restituisce il tipo specificato dell'oggetto univoco
     * @return l'id che rappresenta l'oggetto univocamente
     */
    public E getId();

    /**
     * Restituisce l'array di stringhe composto da tutti i campi
     * del tipo specificato
     * @return array di stringhe dei campi dell'oggetto specificato
     */
    public String[] getFields();
}
