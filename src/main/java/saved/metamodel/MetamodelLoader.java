package saved.metamodel;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Reads an M2 yaml file (architecture.mm.yaml, security.mm.yaml...) into an MPackage,
// building each class with the M3 builders. Knows only M3 words, so it reads any metamodel.

public final class MetamodelLoader {

    private MetamodelLoader(){}

    @SuppressWarnings("unchecked")
    public static MPackage load(String yaml) throws IOException {
        Map<String, Object> root = new YAMLMapper().readValue(yaml, Map.class);
        List<MClass> classes = new ArrayList<>();

        for (Map<String, Object> c : entries(root, "classes"))
            classes.add(loadClass(c));

        return new MPackage(required(root, "package"), required(root, "uri"), List.copyOf(classes));
    }

    // load an M2 file shipped inside the jar (it lives in src/main/resources)
    public static MPackage loadResource(String path){
        try (InputStream in = MetamodelLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalArgumentException("metamodel resource not found: "+ path);
            }
            return load(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read metamodel: " + path, e);
        }
    } 

    // helper for : failure when a required field is missing
    private static String required(Map<String, Object> m, String key){
        
        Object v = m.get(key);
        if (v==null) throw new IllegalArgumentException("metamodel: missing '" + key + "' in " +m);
        return v.toString();
    }

    // the entries under a key (classes, attributes...), none if the key is not there
    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> entries(Map<String, Object> m, String key){
        Object v = m.get(key);
        return v == null ? List.of() : (List<Map<String, Object>>) v;
    }

    // one class block of the file -> one MClass
    private static MClass loadClass(Map<String, Object> c) {
        MClass.Builder b = MClass.builder(required(c, "name"));
        if (optional(c, "abstract", false)) b.abstractClass();   

        List<String> supers = optional(c, "superTypes", List.of()); 
        for (String s : supers) b.superType(s);

        // attributes and references go into the same builder b
        for (Map<String, Object> a : entries(c, "attributes")) loadAttribute(b, a);
        for (Map<String, Object> r : entries(c, "references")) loadReference(b, r);
        
        return b.build();
    }

    // a field that may be missing; then we use the default
    @SuppressWarnings("unchecked")
    private static <T> T optional(Map<String, Object> m, String key, T def) {
        Object v = m.get(key);
        return v == null? def : (T) v;
    }

    // one attribute block -> b.id(...) if it has "id:", otherwise b.attr(...)
    private static void loadAttribute(MClass.Builder b, Map<String, Object> a){
        String name = required(a, "name");
        
        String scope = optional(a, "id", null);
        if (scope!=null){
            b.id(name, IdScope.valueOf(scope));
            return;
        }

        MDataType type = MDataType.valueOf(required(a, "type"));
        int lower = optional(a, "lower", 0);
        int upper = optional(a, "upper", 1);
        List<String> allowed = optional(a, "allowed", List.of());
        b.attr(name, type, lower, upper, allowed.toArray(String[]::new));
    }

    // one reference block -> b.ref(...), then b.annotate(...) for each label (must follow the ref)
    private static void loadReference(MClass.Builder b, Map<String, Object> r){
        b.ref(required(r, "name"), required(r, "target"),
            optional(r, "containment", false), 
            optional(r, "lower", 0), optional(r, "upper", 1));

        Map<String, String> labels = optional(r, "annotations", Map.of());
        for (Map.Entry<String, String> e : labels.entrySet()){
            b.annotate(e.getKey(), e.getValue());
        }
    }

}