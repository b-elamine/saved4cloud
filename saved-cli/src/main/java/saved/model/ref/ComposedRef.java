package saved.model.ref;

import java.util.List;

public record ComposedRef(List<Ref> conditions) implements Ref {}
