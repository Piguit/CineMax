/**
 * Fornisce l'interfaccia utente testuale (TUI) dell'applicazione CineMax.
 * <p>
 * {@link textuserinterface.MenuHelper} gestisce la lettura e la validazione
 * dell'input da console, con cicli di retry e controllo sull'obbligatorietà.
 * {@link textuserinterface.TextUserInterface} implementa il ciclo principale
 * dell'applicazione, differenziando i menu in base al ruolo dell'utente
 * (guest, cliente, proiezionista, bigliettaio, admin) e delegando ogni
 * operazione ai rispettivi service.
 * </p>
 *
 * @see textuserinterface.MenuHelper
 * @see textuserinterface.TextUserInterface
 */
package textuserinterface;