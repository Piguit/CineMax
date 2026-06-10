package repository;
import model.ItemInitializer;
import model.Identifiable;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione generica di un repository su file di testo.
 * Ogni entità è serializzata su una singola riga; i campi sono separati
 * dal carattere {@code ~} e la riga è terminata da {@code \n}.
 * La lettura avviene per blocchi di {@value #PAGE_DIMENSION} byte tramite
 * {@link FileChannel} e {@link ByteBuffer} di Java NIO. Le operazioni di
 * modifica e cancellazione adottano il pattern copy-on-write su file temporaneo.
 *
 * @param <E> tipo dell'identificativo, deve estendere {@link Comparable}
 * @param <F> tipo dell'entità, deve implementare {@link ItemInitializer} e {@link Identifiable}
 */
public class GenericRepository<E extends Comparable<E>, F extends ItemInitializer<F> & Identifiable<E>> {
    /** Dimensione in byte del buffer di lettura/scrittura per le operazioni I/O. */
    private static final int PAGE_DIMENSION = 16384;
    /** Carattere separatore tra i campi di un'entità serializzata. */
    private static final char DIVIDER = '~';
    /** Carattere terminatore di riga; separa entità consecutive nel file. */
    private static final char TERMINATOR = '\n';
    
    /** Nome della cartella in cui risiedono i file di dati. */
    private final String dir = "data";
    /** Istanza prototipo dell'entità, usata come factory per la deserializzazione tramite {@link ItemInitializer#getNewItem}. */
    private final F modelItem;
    /** Path del file di dati principale. */
    private final Path path;
    /** Path del file temporaneo usato durante le operazioni di copy-on-write. */
    private final Path tempPath;

    /** Posizione corrente nel file durante una sessione di lettura sequenziale. */
    private long currentFilePosition;
    /**
     * Buffer che conserva la porzione di riga incompleta a cavallo tra due o più
     * blocchi consecutivi durante la lettura sequenziale.
     */
    private ByteBuffer incompleteTuple;
    /** FileChannel aperto durante una sessione di lettura sequenziale; {@code null} fuori sessione. */
    private FileChannel fileChannel;

    /**
     * Cache dell'identificativo massimo presente nel file.
     * {@code null} se non ancora calcolato o se il repository è vuoto.
     * Viene invalidata a {@code null} da {@link #delete} quando viene rimosso l'elemento con id massimo.
     */
    private E maxId;

    /**
     * Coppia immutabile di due valori di tipo generico.
     * Usata internamente da {@link #getTuples} per restituire
     * contestualmente la lista di tuple complete e il frammento residuo.
     */
    private static class Couple<S, D> {
        private final S sx;
        private final D dx;
        
        public Couple(S sx, D dx) {
            this.sx = sx;
            this.dx = dx;
        }

        public S getSx() {
            return sx;
        }

        public D getDx() {
            return dx;
        }
    }
    
    /**
     * Costruisce il repository per il file specificato nella cartella {@code ../dir}.
     * Se la cartella non esiste viene creata; se il file non esiste viene creato vuoto.
     * @param modelItem istanza prototipo dell'entità, usata come factory di deserializzazione
     * @param fileName  nome del file di dati (es. {@code "movie_repository.txt"})
     * @throws FileException se la cartella o il file non possono essere creati
     */
    public GenericRepository(F modelItem, String fileName) {
        this.modelItem = modelItem;
        this.path = Path.of("../" + dir, fileName);
        this.tempPath = Path.of("../" + dir, "temp_" + fileName);
        File file = path.toFile();
        try {
            Files.createDirectories(Path.of("../" + dir));
        } catch (IOException e) {
            throw new FileException("<!> Non e' stato possibile crare la cartella" + dir + ". Controllare i privilegi della cartella.");
        }
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new FileException("<!> Non e' stato possibile creare il file " + path + ". Controllare i privilegi della cartella.");
            }
    }

    /**
     * Analizza un blocco {@link ByteBuffer} e ne estrae le tuple complete (righe terminate
     * da {@link #TERMINATOR}). Le righe vuote vengono scartate. L'eventuale frammento
     * di riga incompleto a fine buffer viene restituito come secondo elemento della
     * coppia, pronto per essere anteposto al blocco successivo.
     * @param buffer buffer da analizzare, letto dalla posizione 0 fino a {@code capacity()}
     * @return coppia (lista di tuple complete come stringhe, frammento residuo come ByteBuffer)
     */
    private Couple<List<String>, ByteBuffer> getTuples(ByteBuffer buffer) {
        List<String> tuples = new ArrayList<>();
        int chunkLength = buffer.capacity(), start = 0;
        ByteBuffer truncated = ByteBuffer.allocate(0);
        for (int end = 0; end < chunkLength; end++) {
            if (buffer.get(end) == TERMINATOR) {
                String tuple = StandardCharsets.UTF_8.decode(buffer.slice(start, end - start)).toString();
                if (!tuple.isEmpty())
                    tuples.add(tuple);
                start = end + 1;
            }
        }
        try {
            truncated = buffer.slice(start, chunkLength - start);
        } catch (IndexOutOfBoundsException e) {}

        return new Couple<>(tuples, truncated);
    }

    /**
     * Inizializza una sessione di lettura sequenziale: azzera la posizione
     * corrente, svuota il buffer dei frammenti incompleti e apre il
     * {@link FileChannel} in sola lettura. Lancia {@link RuntimeException}
     * se un canale è già aperto (chiamata a {@link #endSequentialReading()}
     * mancante dalla sessione precedente).
     * @throws FileException se il file non può essere aperto in lettura
     */
    public void startSequentialReading() {
        currentFilePosition = 0;
        incompleteTuple = ByteBuffer.allocate(0);
        if (fileChannel != null && fileChannel.isOpen())
            throw new RuntimeException("Method endSequentialReading() must be called first.");
        try {
            fileChannel = FileChannel.open(path, StandardOpenOption.READ);
        } catch (IOException e) {
            throw new FileException("<!> Non e' stato possibile aprire in lettura il file " + path + ". Controllare i privilegi della cartella.");
        }
    }

    /**
     * Chiude il {@link FileChannel} della sessione di lettura corrente.
     * Va sempre invocato in un blocco {@code finally} per garantire il
     * rilascio del canale anche in caso di eccezione durante l'iterazione.
     * @throws FileException se il canale non può essere chiuso
     */
    public void endSequentialReading() {
        try {
            fileChannel.close();
        } catch (IOException e) {
            throw new FileException("<!> Non e' stato possibile chiudere il file " + path + ". Controllare i privilegi della cartella.");
        }
    }

    /**
     * Legge il prossimo blocco di {@link #PAGE_DIMENSION} byte dal file,
     * prepone l'eventuale frammento residuo del blocco precedente ({@link #incompleteTuple}),
     * estrae le tuple complete tramite {@link #getTuples} e le deserializza
     * in oggetti di tipo {@code F}. Aggiorna {@link #incompleteTuple} con
     * il frammento residuo del blocco corrente. Complessità spaziale &Theta;(c),
     * dove c è il numero medio di tuple per blocco.
     * @return lista di entità del blocco corrente; {@code null} se si è raggiunta la fine del file
     * @throws FileException se si verifica un errore di lettura
     */
    public List<F> getNextItems() {
        try {
            while (currentFilePosition < fileChannel.size()) {
                fileChannel.position(currentFilePosition);
                ByteBuffer buffer = ByteBuffer.allocate(incompleteTuple.capacity() + PAGE_DIMENSION);
                buffer.put(incompleteTuple);
                fileChannel.read(buffer);
                currentFilePosition = fileChannel.position();
                
                Couple<List<String>, ByteBuffer> result = getTuples(buffer.slice(0, buffer.position()));
                List<String> tuples = result.getSx();
                incompleteTuple = result.getDx();
                if (tuples.isEmpty()) continue;

                List<F> list = new ArrayList<>();
                for (String tuple : tuples)
                    list.add(modelItem.getNewItem(tuple.split(String.valueOf(DIVIDER))));
                return list;
            }
            return null;
        } catch (IOException e) {
            throw new FileException("<!> Non e' stato possibile leggere dal file " + path + ". Controllare i privilegi della cartella.");
        }
    }

    /**
     * Scansiona sequenzialmente il file alla ricerca dell'entità con
     * l'id specificato. Apre e chiude autonomamente la sessione di lettura.
     * Complessità nel caso peggiore pari a O(n).
     * @param id identificativo da cercare
     * @return l'entità corrispondente, o {@code null} se non presente
     * @throws FileException se si verifica un errore di lettura
     */
    public F findById(E id) {
        startSequentialReading();
        List<F> list = new ArrayList<>();
        try {
            while ((list = getNextItems()) != null) {
                List<F> oneItemList = list.stream().filter(m -> m.getId().equals(id)).toList();
                if (!oneItemList.isEmpty())
                    return oneItemList.getFirst();
            }
        } finally {
            endSequentialReading();
        }
        return null;
    }

    /**
     * Serializza l'entità e la accoda in fondo al file. Prima di scrivere
     * verifica l'assenza di duplicati: se l'id è uguale al massimo corrente
     * l'inserimento viene rifiutato direttamente (O(1)); se è inferiore viene
     * eseguita una {@link #findById} (O(n)) per assicurarsi non sia già occupato;
	 * se è superiore aggiorna la cache {@link #maxId} e procede all'append (O(1)).
     * @param item entità da inserire
     * @return {@code true} se l'inserimento è avvenuto, {@code false} se l'id è già presente
     * @throws FileException se si verifica un errore di scrittura
     */
    public boolean save(F item) {
        E newId = item.getId();
        getMaxId();
        if (maxId != null) {
            if (newId.compareTo(maxId) == 0)
                return false;
            else if (newId.compareTo(maxId) < 0) {
                if (findById(newId) != null)
                    return false;
            } else
                maxId = newId;
        }
        String persistent = "";
        String[] itemFields = item.getFields();
        for (String field : itemFields)
            persistent += DIVIDER + field;
        persistent = persistent.substring(1) + TERMINATOR;
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.APPEND)) {
            ByteBuffer buffer = ByteBuffer.wrap(persistent.getBytes(StandardCharsets.UTF_8));
            channel.write(buffer);
            return true;
        } catch (IOException e) {
            throw new FileException("<!> Non e' stato possibile scrivere sul file " + path + ". Controllare i privilegi della cartella.");
        }
    }

    /**
     * Crea un file temporaneo vuoto in {@link #tempPath}, eliminando
     * l'eventuale residuo di una operazione precedente interrotta.
     * Usato come passo preparatorio da {@link #update} e {@link #delete}.
     * @throws FileException se il file temporaneo non può essere creato
     */
    private void prepareTempFile() {
        try {
            File tempFile = tempPath.toFile();
            if (tempFile.exists())
                tempFile.delete();
            tempFile.createNewFile();
        } catch (IOException e) {
            throw new FileException("<!> Non e' stato possibile creare il file " + tempPath + ". Controllare i privilegi della cartella.");
        }
    }

    /**
     * Sostituisce il file principale con quello temporaneo
     * tramite {@link Files#move} con opzione {@link StandardCopyOption#REPLACE_EXISTING}.
     * Chiamato come passo finale da {@link #update} e {@link #delete}.
     * @throws FileException se lo spostamento del file non riesce
     */
    private void overrideFile() {
        try {
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileException("<!> Non e' stato possibile aggiornare il file " + path + ". Controllare i privilegi della cartella.");
        }
    }

    /**
     * Aggiorna l'entità con lo stesso id di {@code item} riscrivendo il file
     * tramite copy-on-write su {@link #tempPath}. Verifica prima l'esistenza
     * dell'entità con {@link #findById} (O(n)), poi delega la riscrittura a
     * {@link #modify} e infine sostituisce il file originale con {@link #overrideFile}.
     * @param item entità aggiornata; il suo id deve esistere nel repository
     * @return {@code true} se l'aggiornamento è avvenuto, {@code false} se l'entità non è presente
     * @throws FileException se si verifica un errore di I/O
     */
    public boolean update(F item) {
        E id = item.getId();
        getMaxId();
        if (maxId == null || id.compareTo(maxId) > 0 || findById(id) == null)
            return false;

        prepareTempFile();

        try (FileChannel iChannel = FileChannel.open(path, StandardOpenOption.READ);
            FileChannel oChannel = FileChannel.open(tempPath, StandardOpenOption.APPEND)) {
            String[] fields = item.getFields();
            String updatedItem = "";
            for (String field : fields) {
                updatedItem += DIVIDER + field;
            }
            updatedItem = updatedItem.substring(1);
            modify(id, updatedItem, iChannel, oChannel);
        } catch (IOException e) {
            throw new FileException("<!> Non e' stato possibile produrre la versione aggiornata del file " + path + ". Controllare i privilegi della cartella.");
        }
        
        overrideFile();
        
        return true;
    }

    /**
     * Elimina l'entità con l'id specificato riscrivendo il file tramite
     * copy-on-write (come {@link #update}, ma con {@code content = null}
     * passato a {@link #modify}, che omette la riga invece di sostituirla).
     * Se l'id eliminato coincide con {@link #maxId}, la cache viene
     * invalidata ({@code maxId = null}).
     * @param id identificativo dell'entità da eliminare
     * @return {@code true} se l'eliminazione è avvenuta, {@code false} se l'entità non è presente
     * @throws FileException se si verifica un errore di I/O
     */
    public boolean delete(E id) {
        getMaxId();
        if (maxId == null || id.compareTo(maxId) > 0 || findById(id) == null)
            return false;

        prepareTempFile();

        try (FileChannel iChannel = FileChannel.open(path, StandardOpenOption.READ);
            FileChannel oChannel = FileChannel.open(tempPath, StandardOpenOption.APPEND)) {
            modify(id, null, iChannel, oChannel);
        } catch (IOException e) {
            throw new FileException("<!> Non e' stato possibile produrre la versione aggiornata del file " + path + ". Controllare i privilegi della cartella.");
        }
        
        overrideFile();

        if (id.equals(maxId))
            maxId = null;

        return true;
    }

    /**
     * Nucleo del copy-on-write: legge per blocchi da {@code iChannel}, riscrive
     * su {@code oChannel} tutte le tuple eccetto quella con l'id target.
     * Se {@code content} è non nullo, la riga target viene sostituita con
     * {@code content} (update); se è {@code null}, viene semplicemente omessa
     * (delete). La scrittura è ottimizzata per blocchi di {@link #PAGE_DIMENSION}
     * byte tramite {@link #writeData}, che gestisce i blocchi di output
     * troppo corti accumulandoli fino a raggiungere la dimensione della pagina.
     * @param id identificativo dell'entità da modificare o eliminare
     * @param content nuova serializzazione dell'entità (update), o {@code null} per la cancellazione
     * @param iChannel canale di lettura del file originale
     * @param oChannel canale di scrittura del file temporaneo
     * @throws IOException se si verifica un errore su uno dei due canali
     */
    private void modify(E id, String content, FileChannel iChannel, FileChannel oChannel) throws IOException {
        boolean found = false;
        ByteBuffer truncated = ByteBuffer.allocate(0), tooShortBlock = ByteBuffer.allocate(0);
        while (iChannel.position() < iChannel.size()) {
            ByteBuffer iBuffer = ByteBuffer.allocate(truncated.capacity() + PAGE_DIMENSION);
            iBuffer.put(truncated);
            iChannel.read(iBuffer);
                
            Couple<List<String>, ByteBuffer> result = getTuples(iBuffer.slice(0, iBuffer.position()));
            List<String> tuples = result.getSx();
            truncated = result.getDx();
            if (tuples.isEmpty()) continue;

            String oChunk = "";
            for (String tuple : tuples) {
                String[] fields = tuple.split(String.valueOf(DIVIDER));
                if (!modelItem.getNewItem(fields).getId().equals(id))
                    oChunk += tuple + TERMINATOR;
                else {
                    if (content != null && !content.isEmpty())
                        oChunk += content + TERMINATOR;
                    found = true;
                }
            }

            ByteBuffer temp = ByteBuffer.wrap(oChunk.getBytes(StandardCharsets.UTF_8));
            ByteBuffer oBuffer = ByteBuffer.allocate(tooShortBlock.capacity() + temp.capacity());
            oBuffer.put(tooShortBlock);
            oBuffer.put(temp);
            oBuffer.flip();
            
            tooShortBlock = writeData(found, oBuffer, truncated, iChannel, oChannel);
        }
    }

    /**
     * Gestisce la scrittura allineata a pagine del buffer di output durante
     * il copy-on-write. Scrive blocchi interi di {@link #PAGE_DIMENSION} byte
     * finché il buffer lo consente. Se il blocco residuo è troppo corto e il
     * file di input non è esaurito, lo accumula in {@code tooShortBlock} per permettere
     * al chiamante di unirlo al chunk successivo. Una volta comunicatogli che la riga
	 * è stata modificata ({@code found == true}), completa la stesura del file temporaneo,
	 * gestendo iterativamente fino ad esaurimento il chunk da scrivere (dati troncati + nuovo
	 * blocco letto), applicando shift opportuni per scrivere solo a blocchi
	 * di {@link #PAGE_DIMENSION} byte, eccezion fatta per l'eventuale ultimo frammento
	 * più corto.
     * @param found {@code true} se la riga target è già stata elaborata
     * @param oBuffer dati da scrivere
     * @param truncated frammento residuo del blocco di input corrente
     * @param iChannel canale di lettura (per leggere i blocchi successivi se necessario)
     * @param oChannel canale di scrittura del file temporaneo
     * @return eventuale blocco residuo troppo corto, da preporre al chunk successivo
     * @throws IOException se si verifica un errore su uno dei due canali
     */
    private ByteBuffer writeData(boolean found, ByteBuffer oBuffer, ByteBuffer truncated, FileChannel iChannel, FileChannel oChannel) throws IOException {
        int oCapacity = oBuffer.capacity();
        ByteBuffer tooShortBlock = ByteBuffer.allocate(0);
        while (oCapacity >= PAGE_DIMENSION) {
            oChannel.write(oBuffer.slice(0, PAGE_DIMENSION));
            oCapacity = oCapacity - PAGE_DIMENSION;
            if (oCapacity != 0)
                oBuffer = oBuffer.slice(PAGE_DIMENSION, oCapacity);
        }
        if (oCapacity != 0) {
            if (iChannel.position() == iChannel.size())
                oChannel.write(oBuffer);
            else {
                tooShortBlock = oBuffer;
                if (found) {
                    ByteBuffer surplus = ByteBuffer.allocate(tooShortBlock.capacity() + truncated.capacity());
                    surplus.put(tooShortBlock);
                    surplus.put(truncated);
                    surplus.flip();
                    long bytesRead;
                    int extraCapacity = surplus.capacity(), chunkLength;
                    while ((bytesRead = iChannel.position()) < iChannel.size()) {
                        chunkLength = (iChannel.size() - bytesRead < PAGE_DIMENSION) ? (int) (iChannel.size() - bytesRead) : PAGE_DIMENSION;
                        oBuffer = ByteBuffer.allocate(extraCapacity + chunkLength);
                        oBuffer.put(surplus);
                        iChannel.read(oBuffer);
                        oBuffer.flip();
                        surplus = oBuffer.slice(chunkLength, extraCapacity);
                        oChannel.write(oBuffer.slice(0, chunkLength));
                    }
                    if (extraCapacity != 0)
                        oChannel.write(surplus);
                }
            }
        }
        return tooShortBlock;
    }

    /**
     * Restituisce l'id massimo presente nel repository. Alla prima
     * invocazione scansiona l'intero file (O(n)) e memorizza il risultato in {@link #maxId},
	 * che funge da campo cache; le chiamate successive restituiscono il valore
     * cached in O(1), salvo invalidazioni della cache che richiedono la rilettura completa
	 * del file. Restituisce {@code null} se il file è vuoto.
     * @return id massimo, {@code null} se il repository è vuoto
     * @throws FileException se si verifica un errore di lettura
     */
    public E getMaxId() {
        if (maxId != null)
            return maxId;

        startSequentialReading();
        List<F> list = new ArrayList<>();
        E max = null;
        try {
            list = getNextItems();
            if (list != null)
                max = list.getFirst().getId();
            while (list != null) {
                E tmp = list.stream().max((a, b) -> a.getId().compareTo(b.getId())).get().getId();
                max = (max.compareTo(tmp) > 0) ? max : tmp;
                list = getNextItems();
            }
        } finally {
            endSequentialReading();
        }
        maxId = max;
        return max;
    }
}
