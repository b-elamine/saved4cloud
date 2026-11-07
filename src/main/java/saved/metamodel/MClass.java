package saved.metamodel;

import java.util.List;

public record MClass(
        String name,
        boolean abstractClass,
        List<String> superTypes,
        List<MAttribute> attributes,
        List<MReference> references
) {
    public static Builder builder(String name) { return new Builder(name); }

    public static class Builder {
        private final String name;
        private boolean abstractClass = false;
        private final java.util.List<String> superTypes  = new java.util.ArrayList<>();
        private final java.util.List<MAttribute> attrs   = new java.util.ArrayList<>();
        private final java.util.List<MReference> refs    = new java.util.ArrayList<>();

        private boolean lastWasRef = false;   // annotate() may only follow a ref()

        Builder(String name) { this.name = name; }

        public Builder abstractClass()            { this.abstractClass = true; return this; }
        public Builder superType(String s)        { superTypes.add(s); return this; }

        // a normal field; pass allowed values to make it an enum
        public Builder attr(String n, MDataType t, int lo, int hi, String... allowed) {
            attrs.add(new MAttribute(n, t, lo, hi, java.util.List.of(allowed), IdScope.NONE)); lastWasRef=false; return this;
        }
        // the field that names the object: always one string, must be unique
        public Builder id(String n, IdScope scope) {
            attrs.add(new MAttribute(n, MDataType.STRING, 1, 1, java.util.List.of(), scope)); lastWasRef=false; return this;
        }
        public Builder ref(String n, String target, boolean containment, int lo, int hi) {
            refs.add(new MReference(n, target, containment, lo, hi, java.util.Map.of())); lastWasRef=true; return this;
        }

        // label the reference just added, e.g. .ref("via", ...).annotate("role", "mediator")
        public Builder annotate(String key, String value) {
            if (!lastWasRef){
                throw new IllegalStateException(name + ": annotate() must follow a ref()"); 
            }
            int last = refs.size() - 1;
            refs.set(last, refs.get(last).withAnnotation(key, value));
            return this;
        }

        public MClass build() {
            return new MClass(name, abstractClass,
                    java.util.List.copyOf(superTypes),
                    java.util.List.copyOf(attrs),
                    java.util.List.copyOf(refs));
        }
    }
}
