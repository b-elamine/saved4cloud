package saved.metamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Root SAVED metamodel -- architecture, and security.
 *
 * MDE hierarchy:
 *
 *   M3   MPackage / MClass / MAttribute / MReference    (this framework)
 *         |
 *   M2   architecture.mm.yaml       "http://avalon.inria.fr/saved/architecture/"
 *        security.mm.yaml           "http://avalon.inria.fr/saved/security/"
 *        SavedMetamodel             "http://avalon.inria.fr/saved/"  (root, this class)
 *         |
 *   M1   Architecture + SecurityModel instances (loaded from YAML + .secdsl)
 *         |
 *   M0   the running system
 **/

public final class SavedMetamodel {

    // both M2s come from the yaml files inside the jar, loaded once at startup
    public static final MPackage ARCH = MetamodelLoader.loadResource("metamodel/architecture.mm.yaml");
    public static final MPackage SECURITY = MetamodelLoader.loadResource("metamodel/security.mm.yaml");

    /** Flat unified package -- all classes from all sub-metamodels in one view. */
    public static final MPackage INSTANCE = compose();

    private SavedMetamodel() {}

    private static MPackage compose() {
        List<MClass> all = new ArrayList<>();
        all.addAll(ARCH.classes());
        all.addAll(SECURITY.classes());
        return new MPackage("saved", "http://avalon.inria.fr/saved/", all);
    }
}
