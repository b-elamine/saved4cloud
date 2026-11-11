package saved.metamodel;

// A pointer from one class to another. `annotations` are free labels on it,
// e.g. role=mediator, read by the transformation.
public record MReference(
        String name,
        String targetClass,
        boolean containment,
        int lowerBound,
        int upperBound,
        java.util.Map<String, String> annotations
) {
    public boolean required() { return lowerBound >= 1; }
    public boolean many()     { return upperBound == -1 || upperBound > 1; }

    // the value of one label, or null if it isn't there
    public String annotation(String key) {return annotations.get(key);}

    // a copy of this reference with one more label (records can't change)
    public MReference withAnnotation(String key, String value){
        var copy = new java.util.LinkedHashMap <>(annotations);
        copy.put(key, value);
        return new MReference(name, targetClass, containment, lowerBound, upperBound, java.util.Collections.unmodifiableMap(copy));
    }
}
