package saved.model.rule;

import saved.model.ref.Ref;

public record Authentication(Ref sctx, Ref actx, Ref tctx) implements SecurityRule {}
