package com.enonic.xp.web.impl.dispatch.mapping;

import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

public interface ResourceDefinition<T>
{
    int getOrder();

    String getName();

    Set<String> getUrlPatterns();

    Map<String, String> getInitParams();

    T getResource();

    void init( ServletContext context );

    void destroy();
}
