package iterator;

public interface Iterator<TYPE> {
    public boolean hasNext();
    public TYPE next();
    public void first();
}
