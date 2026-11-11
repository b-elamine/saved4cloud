package saved.model.rule;

import saved.model.ref.Ref;

public record Confidentiality(Ref sctx, Ref tctx) implements SecurityRule {}
