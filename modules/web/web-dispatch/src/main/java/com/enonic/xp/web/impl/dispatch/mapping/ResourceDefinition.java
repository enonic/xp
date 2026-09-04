package com.enonic.xp.web.impl.dispatch.mapping;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An immutable, ready-to-serve filter or servlet registration. A definition carries no lifecycle: the
 * resource it wraps is fully initialized by its own component before it is registered, and XP never calls
 * {@code init} or {@code destroy} on it.
 */
public interface ResourceDefinition<T>
{
    int getOrder();

    String getName();

    List<String> getConnectors();

    Set<String> getUrlPatterns();

    Map<String, String> getInitParams();

    T getResource();
}
