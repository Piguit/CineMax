/**
 * <p>
 * Il seguente package gestisce la persistenza dei dati dell'applicazione,
 * con l'implementazione di un repository contenente le informazioni stabilite
 * nelle classi del package Model.
 * </p>
 * <p>
 * Questo sistema prevede un'interfaccia che definisce il contratto delle classi
 * del repository basati su file in base al tipo di entità e al suo identificativo.
 * </p>
 * <p>
 * Prevede delle operazioni di I/O con le funzioni di inserimento, modifica e cancellazione
 * con la lettura dei file sequenziale.
 * </p>
 * @see repository.FileRepository
 * @see repository.FileRepository#insert(java.lang.Object) 
 * @see repository.FileRepository#update(java.lang.Object)
 * @see repository.FileRepository#delete(java.lang.Object) 
 * @see repository.FileException
 * @see repository.GenericRepository
 * @see repository.UserRepository
 * @see repository.UserRepository#FILE_NAME
 * @see repository.MovieRepository
 * @see repository.MovieRepository#FILE_NAME
 * @see repository.ShowRepository
 * @see repository.ShowRepository#FILE_NAME
 * @see repository.ReservationRepository
 * @see repository.ReservationRepository#FILE_NAME
 */
package repository;