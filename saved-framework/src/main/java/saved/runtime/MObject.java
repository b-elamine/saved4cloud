package saved.runtime;

import saved.metamodel.MClass;

// this is the common parent of every generated model class

public interface MObject {
    MClass metaClass(); // tells which metamodel class the object is an instance of
    Object get(String feature); // reads any field by its name
}