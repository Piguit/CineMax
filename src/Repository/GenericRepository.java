package Repository;
import Model.ItemInitializer;
import Model.Identifiable;

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

public class GenericRepository<E extends Comparable<E>, F extends ItemInitializer<F> & Identifiable<E>> {
    private static final int PAGE_DIMENSION = 16384;
    private static final char DIVIDER = '~';
    private static final char TERMINATOR = '\n';
    
    private final F model;
    private final Path path;
    private final Path tempPath;

    private static class Couple<S, D> {
        private S sx;
        private D dx;
        
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
    
    public GenericRepository(F model, String fileName) {
        this.model = model;
        this.path = Path.of("data", fileName);
        this.tempPath = Path.of("data", "temp_" + fileName);
        File file = path.toFile();
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new FileException("<!> Non e' stato possibile creare il file " + path + ". Controllare i privilegi della cartella.");
            }
    }

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

    public List<F> findAll() {
        List<F> list = new ArrayList<>();
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
            ByteBuffer truncated = ByteBuffer.allocate(0);
            while (channel.position() < channel.size()) {
                ByteBuffer buffer = ByteBuffer.allocate(truncated.capacity() + PAGE_DIMENSION);
                buffer.put(truncated);
                channel.read(buffer);
                
                Couple<List<String>, ByteBuffer> result = getTuples(buffer.slice(0, buffer.position()));
                List<String> tuples = result.getSx();
                truncated = result.getDx();
                if (tuples.isEmpty()) continue;

                for (String tuple : tuples)
                    list.add(model.getNewItem(tuple.split(String.valueOf(DIVIDER))));
            }
        } catch (IOException e) {
            throw new FileException("<!> Non e' stato possibile leggere dal file " + path + ". Controllare i privilegi della cartella.");
        }
        return list;
    }

    public F findById(E id) {
        List<F> list = findAll();
        if (list.isEmpty())
            return null;
        List<F> oneItemList = list.stream().filter(m -> m.getId().equals(id)).toList();
        return !oneItemList.isEmpty() ? oneItemList.getFirst() : null;
    }

    public boolean save(F item) {
        if (findById(item.getId()) != null)
            return false;
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

    private void overrideFile() {
        try {
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileException("<!> Non e' stato possibile aggiornare il file " + path + ". Controllare i privilegi della cartella.");
        }
    }

    public boolean update(F item) {
        if (findById(item.getId()) == null)
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
            modify(item.getId(), updatedItem, iChannel, oChannel);
        } catch (IOException e) {
            throw new FileException("<!> Non e' stato possibile produrre la versione aggiornata del file " + path + ". Controllare i privilegi della cartella.");
        }
        
        overrideFile();
        
        return true;
    }

    public boolean delete(E id) {
        if (findById(id) == null)
            return false;

        prepareTempFile();

        try (FileChannel iChannel = FileChannel.open(path, StandardOpenOption.READ);
            FileChannel oChannel = FileChannel.open(tempPath, StandardOpenOption.APPEND)) {
            modify(id, null, iChannel, oChannel);
        } catch (IOException e) {
            throw new FileException("<!> Non e' stato possibile produrre la versione aggiornata del file " + path + ". Controllare i privilegi della cartella.");
        }
        
        overrideFile();

        return true;
    }

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
                if (!model.getNewItem(fields).getId().equals(id))
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

    public E getMaxId() {
        List<F> list = findAll();
        if (!list.isEmpty())
            return list.stream().max((a, b) -> a.getId().compareTo(b.getId())).get().getId();
        else
            return null;
    }
}
