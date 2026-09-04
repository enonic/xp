package com.enonic.xp.web.impl.header;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;

/**
 * The config the wrapped Jetty filter is initialized with. XP does not initialize filters any more, so there
 * is no container-provided config to build on; {@link HeaderFilter} only reads init parameters.
 */
final class FilterConfigImpl
    implements FilterConfig
{
    private final String name;

    private final Map<String, String> initParams;

    FilterConfigImpl( final String name, final Map<String, String> initParams )
    {
        this.name = name;
        this.initParams = Map.copyOf( initParams );
    }

    @Override
    public String getFilterName()
    {
        return this.name;
    }

    @Override
    public ServletContext getServletContext()
    {
        return null;
    }

    @Override
    public String getInitParameter( final String name )
    {
        return this.initParams.get( name );
    }

    @Override
    public Enumeration<String> getInitParameterNames()
    {
        return Collections.enumeration( this.initParams.keySet() );
    }
}
