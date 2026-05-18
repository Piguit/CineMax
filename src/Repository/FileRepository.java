package Repository;

import java.util.List;

public interface FileRepository<F, ID> {
    public List<F> findAll();
    public F findById(ID id);
    public void save(F entity);    // insert or update
    public boolean delete(ID id);
}
