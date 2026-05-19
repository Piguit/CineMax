package Repository;
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

public class MovieRepository implements FileRepository<Movie, Long> {
    private final Path path;
    private final Path tempPath;
    public MovieRepository(String fileName) throws IOException {
        this.path = Path.of("data", fileName);
        this.tempPath = Path.of("data", "temp_"+ fileName);
        File file = path.toFile();
        if (!file.exists())
            file.createNewFile();
    }

    public List<Movie> findAll() {
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
            List<Movie> list = new ArrayList<>();
            String last = "";
            long bytesRead = 0L;
            int pageLength;
            while (channel.position() < channel.size()) {
                pageLength = (channel.size() - bytesRead < 4096) ? (int) (channel.size() - bytesRead) : 4096;
                ByteBuffer buffer = ByteBuffer.allocate(pageLength);
                channel.read(buffer);
                bytesRead += 4096;
                buffer.flip();
                String chunk = last + StandardCharsets.UTF_8.decode(buffer).toString();
                String[] tuples = chunk.split("\n");
                if (tuples.length > 1) {
                    last = tuples[tuples.length - 1];
                    if (last.charAt(last.length() - 1) == '\r') {
                        last = "";
                    }
                }
                //Eliminazione dei '\r' residui
                for (int i = 0; i < tuples.length - 1; i++) {
                    tuples[i] = tuples[i].substring(0, tuples[i].length() - 1);
                }
                if (last.isEmpty()) {
                    tuples[tuples.length - 1] = tuples[tuples.length - 1].substring(0, tuples[tuples.length - 1].length() - 1);
                }
                for (int i = 0; i < tuples.length - 1; i++) {
                    String[] fields = tuples[i].split("\\|");
                    list.add(new Movie(fields));
                }
                if (last.isEmpty()) {
                    String[] fields = tuples[tuples.length - 1].split("\\|");
                    list.add(new Movie(fields));
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();}
        return null;
    }

    public Movie findById(Long id) {
        List<Movie> list = findAll();
        Stream<Movie> stream = list.stream();
        stream = stream.filter(m -> m.getMovieId() == id);
        List<Movie> oneItemList = stream.toList();
        return oneItemList.getFirst();
    }

    public void save(Movie movie) {
        String persistent = "";
        persistent += movie.getMovieId() + "|";
        persistent += movie.getTitle() + "|";
        persistent += movie.getDirector() + "|";
        persistent += movie.getYear() + "|";
        persistent += movie.getGenre() + "|";
        persistent += movie.getRunningTime() + "|";
        persistent += movie.getMinAge() + "\r\n";
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.APPEND)) {
            ByteBuffer buffer = ByteBuffer.wrap(persistent.getBytes(StandardCharsets.UTF_8));
            channel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean delete(Long id) {
        try {
            File tempFile = tempPath.toFile();
            if (tempFile.exists()) tempFile.delete();
            tempFile.createNewFile();
        } catch (IOException e) {e.printStackTrace();}
        int condition = 0;
        try (FileChannel iChannel = FileChannel.open(path, StandardOpenOption.READ);
            FileChannel oChannel = FileChannel.open(tempPath, StandardOpenOption.APPEND)) {
            String last = "";
            long bytesRead = 0L;
            int pageLength;
            ByteBuffer iBuffer;
            while (iChannel.position() < iChannel.size()) {
                pageLength = (iChannel.size() - bytesRead < 4096) ? (int) (iChannel.size() - bytesRead) : 4096;
                iBuffer = ByteBuffer.allocate(pageLength);
                iChannel.read(iBuffer);
                iBuffer.flip();
                bytesRead += 4096;
                if (condition == 0) {
                    //pageLength = (iChannel.size() - bytesRead < 4096) ? (int) (iChannel.size() - bytesRead) : 4096;
                    //ByteBuffer iBuffer = ByteBuffer.allocate(pageLength);
                    //iChannel.read(iBuffer);
                    //bytesRead += 4096;
                    //iBuffer.flip();
                    String iChunk = last + StandardCharsets.UTF_8.decode(iBuffer).toString();
                    String[] tuples = iChunk.split("\n");
                    if (tuples.length > 1) {
                        last = tuples[tuples.length - 1];
                        if (last.charAt(last.length() - 1) == '\r') {
                            last = "";
                        }
                    }
                    //Eliminazione dei '\r' residui
                    for (int i = 0; i < tuples.length; i++) {
                        tuples[i] = tuples[i].substring(0, tuples[i].length() - 1);
                    }
                    String oChunk = "";
                    for (int i = 0; i < tuples.length - 1; i++) {
                        String[] fields = tuples[i].split("\\|");
                        if (new Movie(fields).getMovieId() == id) {
                            condition--;
                        }
                        if (condition == 0)
                            oChunk += tuples[i] + "\r\n";
                    }
                    if (last.isEmpty()) {
                        String[] fields = tuples[tuples.length - 1].split("\\|");
                        if (new Movie(fields).getMovieId() == id) {
                            condition--;
                        }
                        if (condition == 0)
                            oChunk += tuples[tuples.length - 1] + "\r\n";
                    }
                    ByteBuffer oBuffer = ByteBuffer.wrap(oChunk.getBytes(StandardCharsets.UTF_8));
                    oChannel.write(oBuffer);
                } else if (!last.isEmpty()) {
                    byte[] lastBuffer = last.getBytes(StandardCharsets.UTF_8);
                    ByteBuffer widerBuffer = ByteBuffer.allocate(lastBuffer.length + iBuffer.limit());
                    widerBuffer.put(lastBuffer);
                    widerBuffer.put(iBuffer);
                    oChannel.write(widerBuffer.flip());
                } else {
                    oChannel.write(iBuffer.flip());
                }
            }
        } catch (IOException e) {e.printStackTrace();}
        try {
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {e.printStackTrace();}
        return condition < 0;
    }
}
