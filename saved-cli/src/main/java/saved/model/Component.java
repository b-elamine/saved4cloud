package saved.model;

import java.util.List;
import java.util.Map;

public record Component(
        String name,
        String type,
        List<Port> ports,
        List<Component> members,
        Map<String, String> attributes,
        Map<String, Object> properties
) {}
