package saved.model;

import saved.model.rule.SecurityRule;
import java.util.List;

public record SecurityModel(
        List<AttributeType> attributes,
        List<NamedContext> contexts,
        List<SecurityRule> rules
) {}
