package saved.metamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Root SAVED metamodel -- composes core, architecture, and security.
 *
 * Mirrors the structure of the original sam4c.ecore which had three
 * subpackages: core, architecture, security.
 *
 * MDE hierarchy:
 *
 *   M3   MPackage / MClass / MAttribute / MReference    (this framework)
 *         |
 *   M2   CoreMetamodel       "http://avalon.inria.fr/saved/core/"
 *        ArchMetamodel       "http://avalon.inria.fr/saved/architecture/"
 *        SecurityMetamodel   "http://avalon.inria.fr/saved/security/"
 *        SavedMetamodel      "http://avalon.inria.fr/saved/"  (root, this class)
 *         |
 *   M1   Architecture + SecurityModel instances (loaded from YAML + .secdsl)
 *         |
 *   M0   the running system
 *
 * The bridge between the two domain metamodels:
 *   ContextualElement (core) is a supertype of Component (architecture)
 *   AbstractContext (security) is the target type of security rule references
 *   Both ContextualElement and NamedContext extend AbstractContext, which
 *   means architecture elements can be referenced directly in security rules.
 */
public final class SavedMetamodel {

    public static final MPackage CORE     = CoreMetamodel.INSTANCE;
    public static final MPackage ARCH     = ArchMetamodel.INSTANCE;
    public static final MPackage SECURITY = SecurityMetamodel.INSTANCE;

    /** Flat unified package -- all classes from all sub-metamodels in one view. */
    public static final MPackage INSTANCE = compose();

    private SavedMetamodel() {}

    private static MPackage compose() {
        List<MClass> all = new ArrayList<>();
        all.addAll(CORE.classes());
        all.addAll(ARCH.classes());
        all.addAll(SECURITY.classes());
        return new MPackage("saved", "http://avalon.inria.fr/saved/", all);
    }
}
