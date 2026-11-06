package saved.metamodel;

import java.util.List;

// A field on an MClass. `allowed` is the value set for an enum STRING field
// (empty means free text).
public record MAttribute(
        String name,
        MDataType type,
        int lowerBound,
        int upperBound,
        List<String> allowed,
        IdScope idScope
) {
    // no enum constraint
    public MAttribute(String name, MDataType type, int lowerBound, int upperBound) {
        this(name, type, lowerBound, upperBound, List.of(), IdScope.NONE);
    }

    public boolean required()  { return lowerBound >= 1; }
    public boolean isId()      {return idScope != IdScope.NONE;}
    public boolean many()      { return upperBound == -1 || upperBound > 1; }
    public boolean isEnum()    { return type == MDataType.STRING && !allowed.isEmpty(); }
}
