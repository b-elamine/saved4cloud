package saved.model.rule;

public sealed interface SecurityRule
        permits Confidentiality, Integrity, Isolation, Authentication, Authorization, Availability {}
