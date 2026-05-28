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

public class GenericRepository<E, F extends ItemInitializer<F> & Identifiable<E>> {
    private final F model;
    private final Path path;
    private final Path tempPath;

    private final char DIVIDER = '~';
    private final char TERMINATOR = '\n';

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

    public GenericRepository(F model, String fileName) throws IOException {
        this.model = model;
        this.path = Path.of("data", fileName);
        this.tempPath = Path.of("data", "temp_" + fileName);
        File file = path.toFile();
        if (!file.exists())
            file.createNewFile();
    }

    private Couple<List<String>, ByteBuffer> getTuples(ByteBuffer buffer) throws IOException {
        List<String> tuples = new ArrayList<>();
        int chunkLength = buffer.capacity(), beginning = 0;
        ByteBuffer truncated = ByteBuffer.allocate(0);
        for (int end = 0; end < chunkLength; end++) {
            if (buffer.get(end) == (byte) TERMINATOR) {
                String tuple = StandardCharsets.UTF_8.decode(buffer.slice(beginning, end - beginning)).toString();
                if (!tuple.isEmpty())
                    tuples.add(tuple);
                beginning = end + 1;
            }
        }
        try {
            truncated = buffer.slice(beginning, chunkLength - beginning);
        } catch (IndexOutOfBoundsException e) {}

        return new Couple<>(tuples, truncated);
    }

    public List<F> findAll() {
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
            List<F> list = new ArrayList<>();
            ByteBuffer buffer , truncated = ByteBuffer.allocate(0);
            while (channel.position() < channel.size()) {
                buffer = ByteBuffer.allocate(truncated.capacity() + 4096);
                buffer.put(truncated);
                channel.read(buffer);
                
                Couple<List<String>, ByteBuffer> result = getTuples(buffer.slice(0, buffer.position()));
                List<String> tuples = result.getSx();
                truncated = result.getDx();
                if (tuples.isEmpty()) continue;

                for (String tuple: tuples)
                    list.add(model.getNewItem(tuple.split(String.valueOf(DIVIDER))));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public F findById(E id) {
        List<F> list = findAll();
        List<F> oneItemList = list.stream().filter(m -> m.getId().equals(id)).toList();
        return oneItemList.isEmpty() ? null : oneItemList.getFirst();
    }

    public boolean save(F item) {
        if (findById(item.getId()) != null)
            return false;
        String persistent = "";
        String[] itemFields = item.getFields();
        for (String field: itemFields)
            persistent += DIVIDER + field;
        persistent = persistent.substring(1) + TERMINATOR;
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

    public boolean update(F item) {
        if (findById(item.getId()) == null)
            return false;
        try {
            File tempFile = tempPath.toFile();
            if (tempFile.exists()) tempFile.delete();
            tempFile.createNewFile();
        } catch (IOException e) {e.printStackTrace();}
        
        boolean result = false;
        try (FileChannel iChannel = FileChannel.open(path, StandardOpenOption.READ);
            FileChannel oChannel = FileChannel.open(tempPath, StandardOpenOption.APPEND)) {
            String[] fields = item.getFields();
            String updatedItem = "";
            for (String field: fields) {
                updatedItem += DIVIDER + field;
            }
            updatedItem = updatedItem.substring(1);
            result = modify(item.getId(), updatedItem, iChannel, oChannel);
        } catch (IOException e) {e.printStackTrace();}
        
        try {
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {e.printStackTrace();}
        return result;
    }

    public boolean delete(E id) {
        if (findById(id) == null)
            return false;
        try {
            File tempFile = tempPath.toFile();
            if (tempFile.exists()) tempFile.delete();
            tempFile.createNewFile();
        } catch (IOException e) {e.printStackTrace();}
        
        boolean result = false;
        try (FileChannel iChannel = FileChannel.open(path, StandardOpenOption.READ);
            FileChannel oChannel = FileChannel.open(tempPath, StandardOpenOption.APPEND)) {
            result = modify(id, null, iChannel, oChannel);
        } catch (IOException e) {e.printStackTrace();}
        
        try {
            if (result)
                Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {e.printStackTrace();}
        return result;
    }

    private boolean modify(E id, String content, FileChannel iChannel, FileChannel oChannel) throws IOException {
        boolean found = false;
        ByteBuffer iBuffer, oBuffer, truncated = ByteBuffer.allocate(0), tooShortBlock = ByteBuffer.allocate(0);
        while (!found) {
            if (iChannel.position() == iChannel.size())
                return false;
            iBuffer = ByteBuffer.allocate(truncated.capacity() + 4096);
            iBuffer.put(truncated);
            iChannel.read(iBuffer);
                
            Couple<List<String>, ByteBuffer> result = getTuples(iBuffer.slice(0, iBuffer.position()));
            List<String> tuples = result.getSx();
            truncated = result.getDx();
            if (tuples.isEmpty()) continue;

            String oChunk = "";
            for (String tuple: tuples) {
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
            oBuffer = ByteBuffer.allocate(tooShortBlock.capacity() + temp.capacity());
            oBuffer.put(tooShortBlock);
            oBuffer.put(temp);
            oBuffer.flip();
            
            tooShortBlock = writeData(found, oBuffer, truncated, iChannel, oChannel);
        }
        return true;
    }

    private ByteBuffer writeData(boolean found, ByteBuffer oBuffer, ByteBuffer truncated, FileChannel iChannel, FileChannel oChannel) throws IOException {
        int oCapacity = oBuffer.capacity();
        ByteBuffer tooShortBlock = ByteBuffer.allocate(0);
        while (oCapacity >= 4096) {
            oChannel.write(oBuffer.slice(0, 4096));
            oCapacity = oCapacity - 4096;
            if (oCapacity != 0)
                oBuffer = oBuffer.slice(4096, oCapacity);
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
                        chunkLength = (iChannel.size() - bytesRead < 4096) ? (int) (iChannel.size() - bytesRead) : 4096;
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
}
