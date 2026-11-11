package saved.model;

import java.util.Map;

public record Implementation(
        String name,
        String runtime,
        String image,
        Map<String, Object> resources,
        Map<String, Object> config
) {}
