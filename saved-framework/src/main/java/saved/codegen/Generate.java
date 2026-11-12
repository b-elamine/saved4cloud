package saved.codegen;

import saved.metamodel.MetamodelLoader;

import java.nio.file.Files;
import java.nio.file.Path;

// run the generator from the command line (Maven will call this at every build, step 3e)
// usage: Generate <yaml file> <java package> <resource name> <output folder>
public final class Generate {
    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            System.err.println("usage: Generate <yaml file> <java package> <resource name> <output folder>");
            System.exit(1);
        }
        var pkg = MetamodelLoader.load(Files.readString(Path.of(args[0])));
        new JavaGenerator(pkg, args[1], args[2]).generate(Path.of(args[3]));
        System.out.println("generated " + pkg.classes().size() + " classes into " + args[3]);
    }
}