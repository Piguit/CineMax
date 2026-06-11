package model;
/**
 * L'interfaccia {@code ItemInitializer<E>} definisce il contratto che
 * le classi che la implementano devono rispettare.
 * @param <E> tipo che sarà inserito in base alla classe che lo implementa
 * @author Edo Hodzic 761022
 * @author Piergiorgio Tomaciello 761013
 * CO
 */
public interface ItemInitializer<E> {
    /**
     * Istanzia e restituisce un nuovo oggetto del tipo specificato
     * a partire da un array di stringhe contenente i suoi dati.
     * @param array array di stringhe con i campi ordinati dell'oggetto
     * @return oggetto istanziato del tipo generico {@code E}
     */
    public E getNewItem(String[] array);
}
