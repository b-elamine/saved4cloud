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
 *   M2   ArchMetamodel       "http://avalon.inria.fr/saved/architecture/"
 *        SecurityMetamodel   "http://avalon.inria.fr/saved/security/"
 *        SavedMetamodel      "http://avalon.inria.fr/saved/"  (root, this class)
 *         |
 *   M1   Architecture + SecurityModel instances (loaded from YAML + .secdsl)
 *         |
 *   M0   the running system
 **/

public final class SavedMetamodel {

    public static final MPackage ARCH     = ArchMetamodel.INSTANCE;
    public static final MPackage SECURITY = SecurityMetamodel.INSTANCE;

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
