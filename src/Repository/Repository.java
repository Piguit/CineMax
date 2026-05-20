package Repository;
import Model.ItemInitializer;
import Model.Identifiable;
import Model.Movie;

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

public class Repository<E, F extends ItemInitializer<F> & Identifiable<E>> {
    private final F model;
    private final Path path;
    private final Path tempPath;

    
    private final char DIVIDER = '~';
    private final char TERMINATOR = '\n';
    public Repository(F model, String fileName) throws IOException {
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
        return oneItemList.getFirst();
    }

    public void save(F item) {
        String persistent = "";
        String[] itemFields = item.getFields();
        for (int i = 0; i < itemFields.length - 1; i++)
            persistent += itemFields[i] + "~";
        persistent += itemFields[itemFields.length - 1] + TERMINATOR;
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.APPEND)) {
            ByteBuffer buffer = ByteBuffer.wrap(persistent.getBytes(StandardCharsets.UTF_8));
            channel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean delete(E id) {
        try {
            File tempFile = tempPath.toFile();
            if (tempFile.exists()) tempFile.delete();
            tempFile.createNewFile();
        } catch (IOException e) {e.printStackTrace();}
        
        boolean result = false;
        try (FileChannel iChannel = FileChannel.open(path, StandardOpenOption.READ);
            FileChannel oChannel = FileChannel.open(tempPath, StandardOpenOption.APPEND)) {
            String last = tryToDelete(id, iChannel, oChannel);
            if (last != null && !last.isEmpty()) {
                handleSplitTuple(last, iChannel, oChannel);
            }
            copyRemainingData(iChannel, oChannel);
            result = oChannel.size() < iChannel.size();
        } catch (IOException e) {e.printStackTrace();}
        
        try {
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {e.printStackTrace();}
        return result;
    }

    private String tryToDelete(E id, FileChannel iChannel, FileChannel oChannel) throws IOException {
        boolean incompleteTuple = false, found = false;
        String last = "";
        int pageLength, completeTuples;
        long bytesRead;
        ByteBuffer iBuffer;
        while (true) {
            if ((bytesRead = iChannel.position()) == iChannel.size()) return null;
            if (found) return last;
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
                if (model.getNewItem(fields).getId().equals(id)) {
                    oChunk += tuples[i] + "\n";
                } else {
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
}
