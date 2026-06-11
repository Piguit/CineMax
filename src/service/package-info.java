/**
 * <p>
 * Il seguente package si occupa della logica con i servizi fondamentali dell'applicazione.
 * </p>
 * <p>
 * Si occupa principalmente della gestione dell'autenticazione e registrazione
 * degli utenti che devono eseguire l'accesso, dove si gestisce la verifica
 * di sicurezza delle credenziali con i vari vincoli imposti per le prenotazioni.
 * Viene utilizzata una eccezione specifica per segnalare gli errori prevedibili
 * e comunicabili dell'utente.
 * </p>
 * @see service.Authentication
 * @see service.Authentication#signIn(java.lang.String, java.lang.String) 
 * @see service.Authentication#signUp(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.time.LocalDate, java.lang.String)
 * @see service.ShowService
 * @see service.ShowService#searchAndPrintShows(java.lang.String, java.lang.String, java.time.LocalDate, java.time.LocalDate, java.lang.Float, java.lang.Float)
 * @see service.MovieService
 * @see service.MovieService#searchAndPrintMovies(java.lang.String, java.lang.String, java.lang.Short)
 * @see service.ReservationService
 * @see service.ReservationService#searchAndPrintReservations(java.lang.Long, java.lang.String, java.lang.String, java.lang.String, java.time.LocalDate, java.time.LocalDate)
 * @see service.PromptException
 */
package service;