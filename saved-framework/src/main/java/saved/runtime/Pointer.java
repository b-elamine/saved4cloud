package saved.runtime;

// reference to a non-contained object
// becomes an object when asked (.resolve)

public record Pointer<T extends MObject>(String name, Class<T> type) {
    public T resolve(ModelIndex index){
        return index.find(name, type);
    }
}