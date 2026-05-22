package Repository;
import Model.ItemInitializer;
import Model.Identifiable;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class GenericRepository<E, F extends ItemInitializer<F> & Identifiable<E>> {
    private final F model;
    private final Path path;
    private final Path tempPath;
    
    private final char DIVIDER = '~';
    private final char TERMINATOR = '\n';
    public GenericRepository(F model, String fileName) throws IOException {
        this.model = model;
        this.path = Path.of("data", fileName);
        this.tempPath = Path.of("data", "temp_" + fileName);
        File file = path.toFile();
        if (!file.exists())
            file.createNewFile();
    }

    public List<F> findAll() {
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
            List<F> list = new ArrayList<>();
            boolean incompleteTuple = false;
            String last = "";
            int pageLength, completeTuples;
            long bytesRead;
            ByteBuffer buffer;
            while ((bytesRead = channel.position()) < channel.size()) {
                pageLength = (channel.size() - bytesRead < 4096) ? (int) (channel.size() - bytesRead) : 4096;
                buffer = ByteBuffer.allocate(pageLength);
                channel.read(buffer);
                buffer.flip();
                String chunk = last + StandardCharsets.UTF_8.decode(buffer).toString();
                
                if (chunk.charAt(chunk.length() - 1) != TERMINATOR)
                    incompleteTuple = true;
                String[] tuples = chunk.split(String.valueOf(TERMINATOR));
                if (incompleteTuple) {
                    completeTuples = tuples.length - 1;
                    last = tuples[completeTuples];
                    incompleteTuple = false;
                } else {
                    completeTuples = tuples.length;
                    last = "";
                }

                for (int i = 0; i < completeTuples; i++) {
                    String[] fields = tuples[i].split(String.valueOf(DIVIDER));
                    list.add(model.getNewItem(fields));
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();}
        return null;
    }

    public F findById(E id) {
        List<F> list = findAll();
        Stream<F> stream = list.stream();
        stream = stream.filter(m -> m.getId().equals(id));
        List<F> oneItemList = stream.toList();
        return oneItemList.isEmpty() ? null : oneItemList.getFirst();
    }

    public boolean save(F item) {
        if (findById(item.getId()) != null)
            return false;
        String persistent = "";
        String[] itemFields = item.getFields();
        for (int i = 0; i < itemFields.length - 1; i++)
            persistent += itemFields[i] + "~";
        persistent += itemFields[itemFields.length - 1] + TERMINATOR;
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.APPEND)) {
            ByteBuffer buffer = ByteBuffer.wrap(persistent.getBytes(StandardCharsets.UTF_8));
            channel.write(buffer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Temporaneo
        return false;
    }

    public boolean delete(E id) {
        try {
            File tempFile = tempPath.toFile();
            if (tempFile.exists()) tempFile.delete();
            tempFile.createNewFile();
        } catch (IOException e) {e.printStackTrace();}
        
        Couple<Boolean, String> results = null;
        try (FileChannel iChannel = FileChannel.open(path, StandardOpenOption.READ);
            FileChannel oChannel = FileChannel.open(tempPath, StandardOpenOption.APPEND)) {
            results = tryToModify(id, null, iChannel, oChannel);
            if (results.getDx() != null && !results.getDx().isEmpty()) {
                handleSplitTuple(results.getDx(), iChannel, oChannel);
            }
            copyRemainingData(iChannel, oChannel);
        } catch (IOException e) {e.printStackTrace();}
        
        try {
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {e.printStackTrace();}
        return results.getSx();
    }

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

    private Couple<Boolean, String> tryToModify(E id, String content, FileChannel iChannel, FileChannel oChannel) throws IOException {
        boolean incompleteTuple = false, found = false;
        String last = "";
        int pageLength, completeTuples;
        long bytesRead;
        ByteBuffer iBuffer;
        while (true) {
            if ((bytesRead = iChannel.position()) == iChannel.size()) return new Couple<>(found, null);
            if (found) return new Couple<>(found, last);
            pageLength = (iChannel.size() - bytesRead < 4096) ? (int) (iChannel.size() - bytesRead) : 4096;
            iBuffer = ByteBuffer.allocate(pageLength);
            iChannel.read(iBuffer);
            iBuffer.flip();
            String iChunk = last + StandardCharsets.UTF_8.decode(iBuffer).toString();
            
            if (iChunk.charAt(iChunk.length() - 1) != TERMINATOR)
                incompleteTuple = true;
            String[] tuples = iChunk.split(String.valueOf(TERMINATOR));
            if (incompleteTuple) {
                completeTuples = tuples.length - 1;
                last = tuples[completeTuples];
                incompleteTuple = false;
            } else {
                completeTuples = tuples.length;
                last = "";
            }

            String oChunk = "";
            for (int i = 0; i < completeTuples; i++) {
                String[] fields = tuples[i].split(String.valueOf(DIVIDER));
                if (!model.getNewItem(fields).getId().equals(id))
                    oChunk += tuples[i] + TERMINATOR;
                else if (content == null || content.isEmpty())
                    found = true;
                else {
                    oChunk += content + TERMINATOR;
                    found = true;
                }
            }
            ByteBuffer oBuffer = ByteBuffer.wrap(oChunk.getBytes(StandardCharsets.UTF_8));
            oChannel.write(oBuffer);
        }
    }

    private void handleSplitTuple(String last, FileChannel iChannel, FileChannel oChannel) throws IOException {
        long bytesRead = iChannel.position();
        int pageLength = (iChannel.size() - bytesRead < 4096) ? (int) (iChannel.size() - bytesRead) : 4096;
        ByteBuffer iBuffer = ByteBuffer.allocate(pageLength);
        iChannel.read(iBuffer);
        byte[] lastBuffer = last.getBytes(StandardCharsets.UTF_8);
        ByteBuffer widerBuffer = ByteBuffer.allocate(lastBuffer.length + pageLength);
        widerBuffer.put(lastBuffer);
        widerBuffer.put(iBuffer.flip());
        oChannel.write(widerBuffer.flip());
    }

    private void copyRemainingData(FileChannel iChannel, FileChannel oChannel) throws IOException {
        int pageLength;
        long bytesRead;
        ByteBuffer iBuffer;
        while ((bytesRead = iChannel.position()) < iChannel.size()) {
            pageLength = (iChannel.size() - bytesRead < 4096) ? (int) (iChannel.size() - bytesRead) : 4096;
            iBuffer = ByteBuffer.allocate(pageLength);
            iChannel.read(iBuffer);
            oChannel.write(iBuffer.flip());
        }
    }

    public boolean update(F item) {
        try {
            File tempFile = tempPath.toFile();
            if (tempFile.exists()) tempFile.delete();
            tempFile.createNewFile();
        } catch (IOException e) {e.printStackTrace();}
        
        Couple<Boolean, String> results = null;
        try (FileChannel iChannel = FileChannel.open(path, StandardOpenOption.READ);
            FileChannel oChannel = FileChannel.open(tempPath, StandardOpenOption.APPEND)) {
            String[] fields = item.getFields();
            String updatedItem = "";
            for (String field: fields) {
                updatedItem += DIVIDER + field;
            }
            updatedItem = updatedItem.substring(1);
            results = tryToModify(item.getId(), updatedItem, iChannel, oChannel);
            if (results.getDx() != null && !results.getDx().isEmpty()) {
                handleSplitTuple(results.getDx(), iChannel, oChannel);
            }
            copyRemainingData(iChannel, oChannel);
        } catch (IOException e) {e.printStackTrace();}
        
        try {
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {e.printStackTrace();}
        return results.getSx();
    }
}
