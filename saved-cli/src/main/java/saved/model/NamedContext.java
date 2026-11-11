package saved.model;

import saved.model.ref.Ref;
import java.util.List;

public record NamedContext(String name, List<Ref> conditions) {}
