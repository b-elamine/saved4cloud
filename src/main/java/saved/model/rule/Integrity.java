package saved.model.rule;

import saved.model.ref.Ref;

public record Integrity(Ref sctx, Ref tctx) implements SecurityRule {}
