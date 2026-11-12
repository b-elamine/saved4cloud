package saved.codegen;

import saved.metamodel.MClass;
import saved.metamodel.MPackage;
import saved.metamodel.MAttribute;
import saved.metamodel.MReference;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

// turns one metamodel into java files (abstract -> interface, concrete -> record)
public final class JavaGenerator {

    private final MPackage pkg;          // the metamodel
    private final String javaPackage;    // where the files go
    private final String resource;       // the yaml it came from

    public JavaGenerator(MPackage pkg, String javaPackage, String resource) {
        this.pkg = pkg;
        this.javaPackage = javaPackage;
        this.resource = resource;
    }

    // writes everything: package class + one file per class
    public void generate(Path outDir) throws IOException {
        Path dir = outDir.resolve(javaPackage.replace(".", "/"));
        Files.createDirectories(dir);

        writeFile(dir, packageClassName(), packageClass());
        for (MClass c : pkg.classes()) {
            writeFile(dir, c.name(), c.abstractClass() ? interfaceFor(c) : recordFor(c));
        }
    }

    // architecture -> ArchitecturePackage
    private String packageClassName() {
        return Character.toUpperCase(pkg.name().charAt(0)) + pkg.name().substring(1) + "Package";
    }

    // loads the metamodel once, records use it for metaClass()
    private String packageClass() {
        return "public final class " + packageClassName() + " {\n"
             + "    public static final saved.metamodel.MPackage INSTANCE =\n"
             + "        saved.metamodel.MetamodelLoader.loadResource(\"" + resource + 
            "\");\n"
             + "    private " + packageClassName() + "() {}\n"
             +"}\n";
    }

    // same header on every file
    private void writeFile(Path dir, String className, String body) throws IOException {
        String header = "// GENERATED from " + resource + " -- do not edit\n"
                        + "package " + javaPackage + ";\n\n";
        Files.writeString(dir.resolve(className + ".java"), header + body);
    }

    // M2 types to java types
    private String javaType(MAttribute a) {
        String one = switch (a.type()) {
            case STRING -> "String";
            case INT -> "Integer";
            case BOOLEAN -> "Boolean";
            case MAP -> "java.util.Map<String, Object>";
            case LIST -> "java.util.List<String>";
        };
        return a.many() ? "java.util.List<" + one + ">" : one;
    }

    // javatype of a ref : contained -> the object itself else a Pointer
    private String javaType(MReference r) {
        String one = r.containment() ? r.targetClass() : "saved.runtime.Pointer<" + r.targetClass() + ">";
        return r.many() ? "java.util.List<" + one + ">" : one;
    }

    private String interfaceFor(MClass c) {
        List<String> children = childrenOf(c);
        String parents = c.superTypes().isEmpty() ? "saved.runtime.MObject" : String.join(", ", c.superTypes());

        StringBuilder sb = new StringBuilder();
        sb.append("public ").append(children.isEmpty() ? "" : "sealed ").append("interface ").append(c.name()).append(" extends ").append(parents);
        if (!children.isEmpty()) sb.append(" permits ").append(String.join(", ", children));
        sb.append(" {\n");

        for (MAttribute a : c.attributes())
            sb.append("    ").append(javaType(a)).append(' ').append(a.name()).append("();\n");
        for (MReference r : c.references())
            sb.append("    ").append(javaType(r)).append(' ').append(r.name()).append("();\n");

        sb.append("}\n");
        return sb.toString();
    }

    // the classes that name c as a direct parent
    private List<String> childrenOf(MClass c) {
        List<String> out = new ArrayList<>();
        for (MClass k : pkg.classes())
            if (k.superTypes().contains(c.name())) out.add(k.name());
        return out;
    }
    private String recordFor(MClass c) {
        Map<String, String> fields = fieldsOf(c);
        String parents = c.superTypes().isEmpty() ? "saved.runtime.MObject"
                                                  : String.join(", ", c.superTypes());

        // the header: "public record App(String exposure, ...) implements Deployable {"
        List<String> params = new ArrayList<>();
        for (Map.Entry<String, String> f : fields.entrySet())
            params.add(f.getValue() + " " + f.getKey());

        StringBuilder sb = new StringBuilder();
        sb.append("public record ").append(c.name())
          .append("(").append(String.join(", ", params)).append(")")
          .append(" implements ").append(parents).append(" {\n\n");

        // metaClass(): its own MClass, from the package class
        sb.append("    public saved.metamodel.MClass metaClass() {\n")
          .append("        return ").append(packageClassName())
          .append(".INSTANCE.require(\"").append(c.name()).append("\");\n")
          .append("    }\n\n");

        // get(): one case per field
        sb.append("    public Object get(String feature) {\n")
          .append("        return switch (feature) {\n");
        for (String name : fields.keySet())
            sb.append("            case \"").append(name).append("\" -> ").append(name).append(";\n");
        sb.append("            default -> throw new IllegalArgumentException(\"")
          .append(c.name()).append(" has no feature \" + feature);\n")
          .append("        };\n")
          .append("    }\n")
          .append("}\n");

        return sb.toString();
    }

    // every field of c, own + inherited, as name -> java type (each name once)
    private Map<String, String> fieldsOf(MClass c) {
        Map<String, String> out = new LinkedHashMap<>();
        for (MAttribute a : pkg.allAttributes(c.name())) addField(out, c, a.name(), javaType(a));
        for (MReference r : pkg.allReferences(c.name())) addField(out, c, r.name(), javaType(r));
        return out;
    }

    // the same name from two parents is fine once if the type is the same, an error if not
    private void addField(Map<String, String> out, MClass c, String name, String type) {
        String old = out.putIfAbsent(name, type);
        if (old != null && !old.equals(type))
            throw new IllegalStateException(c.name() + "." + name + ": two parents give different types");
    }
}