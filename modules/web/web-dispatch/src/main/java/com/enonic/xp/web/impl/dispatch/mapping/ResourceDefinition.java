package com.enonic.xp.web.impl.dispatch.mapping;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An immutable filter or servlet registration, ready to serve requests. The resource it wraps is initialized
 * by its own component; the servlet lifecycle methods are not called on it.
 */
public interface ResourceDefinition<T>
{
    int getOrder();

    String getName();

    List<String> getConnectors();

    Set<String> getUrlPatterns();

    Map<String, String> getInitParams();

    T getResource();

    /**
     * Returns true if this definition serves the given request path.
     *
     * @param path the decoded path of the request within the context
     */
    boolean matches( String path );
}
