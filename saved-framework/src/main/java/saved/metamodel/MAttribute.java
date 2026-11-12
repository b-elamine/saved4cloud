package saved.metamodel;

import java.util.List;

// a simple field of a class; allowed = enum values (empty = any text)
public record MAttribute(
        String name,
        MDataType type,
        int lowerBound,
        int upperBound,
        List<String> allowed,
        IdScope idScope
) {
    public boolean required()  { return lowerBound >= 1; }
    public boolean isId()      { return idScope != IdScope.NONE; }
    public boolean many()      { return upperBound == -1 || upperBound > 1; }
    public boolean isEnum()    { return type == MDataType.STRING && !allowed.isEmpty(); }
}
