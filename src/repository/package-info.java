/**
 * <p>
 * Il seguente package gestisce la persistenza dei dati dell'applicazione,
 * con l'implementazione di un repository contenente le informazioni stabilite
 * nelle classi del package Model.
 * </p>
 * <p>
 * Questo sistema prevede un'interfaccia che definisce il contratto delle classi
 * per la persistenza su file in base al tipo di entità gestita e al suo identificativo.
 * </p>
 * <p>
 * Prevede delle operazioni di I/O con le funzioni di inserimento, proiezione, modifica e cancellazione
 * con lettura e scrittura sequenziali dei file.
 * </p>
 * <p>
 * {@link repository.FileException} è un'eccezione unchecked lanciata in caso di errori critici relativi all'I/O su file.
 * </p>
 * @see repository.FileRepository
 * @see repository.FileException
 * @see repository.GenericRepository
 * @see repository.UserRepository
 * @see repository.MovieRepository
 * @see repository.ShowRepository
 * @see repository.ReservationRepository
 */
package repository;