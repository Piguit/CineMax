package Repository;

import java.util.List;

public interface FileRepository<F, ID> {
    //public List<F> findAll();
    public F findById(ID id);
    public boolean insert(F entity);
    public boolean delete(ID id);
    public ID getMaxId();
    public List<F> getNextItems();
    public void startSequentialReading();
    public void endSequentialReading();
}
