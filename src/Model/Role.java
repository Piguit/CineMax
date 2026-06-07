package Model;

/**
 * La classe enumerativa {@code Role} si utilizza per definire
 * i ruoli dell'utente all'interno del sistema.
 * <p>
 * I ruoli in questione si suddividono tra: {@code CLIENT}, {@code PROJECTIONIST},
 * {@code BOXOFFICECLERK} e {@code ADMIN}
 * </p>
 */
public enum Role {
    /** Rappresenta il cliente nel sistema. I clienti registrati possono eseguire
     * la visualizzazione, inserimento, modifica e cancellazione delle proprie prenotazioni.
     * I clienti non registrati invece possono cercare e visualizzare i dettagli delle proiezioni.*/
    CLIENT,
    /** Rappresenta il proiezionista nel sistema. Ha l'accesso alle funzionalità
     * relative all'inserimento dei film e della data e costo del biglietto per ogni proiezione
     * con la possibilità di eliminare una proiezione e modificarne la data.*/
    PROJECTIONIST,
    /**
     * Rappresenta il bigliettaio nel sistema. Ha l'accesso alle funzionalità relative
     * alla ricerca e visualizzazione delle prenotazioni in una data specifica.
     */
    BOXOFFICECLERK,
    /**
     * Rappresenta l'amministratore del sistema. Ha l'accesso a tutte le funzionalità.
     */
    ADMIN;
}