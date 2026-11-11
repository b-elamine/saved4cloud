package saved.runtime;

import java.util.HashMap;
import java.util.Map;

// creating a sort of a table "name" --> "object" for the whole model (one for sec one for arch)
// we resolve pointers through it

public final class ModelIndex {
    private final Map<String, MObject> byName = new HashMap<>();

    // record object under its name (no duplicate)
    public boolean add(String name, MObject obj) {
        return byName.putIfAbsent(name, obj) == null;
    }

    // return the object with the name "name" if it is a <T> (MObject)
    public <T extends MObject> T find(String name, Class<T> type) {
        MObject found = byName.get(name);
        return type.isInstance(found) ? type.cast(found) : null;
    }
}