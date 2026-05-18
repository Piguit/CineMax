package Repository;
import Model.Movie;

import javax.imageio.stream.IIOByteBuffer;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class MovieRepository implements FileRepository<Movie, Long> {
    private final Path path;
    public MovieRepository(String fileName) throws IOException {
        this.path = Path.of("data", fileName);
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
                pageLength = (channel.size() - bytesRead < 4096) ? (int) channel.size() : 4096;
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
                for (int i = 0; i < tuples.length - 1; i++) {
                    String[] fields = tuples[i].split("|");
                    list.add(new Movie(fields));
                }
                if (last.isEmpty()) {
                    String[] fields = tuples[tuples.length - 1].split("|");
                    list.add(new Movie(fields));
                }
            }
            return list;
        } catch (IOException e) {e.printStackTrace();}
        return null;
    }

    public Movie findById(Long id) {

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

    }

}
