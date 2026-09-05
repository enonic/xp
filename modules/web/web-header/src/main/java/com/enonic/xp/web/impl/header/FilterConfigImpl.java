package com.enonic.xp.web.impl.header;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;

/**
 * The {@link FilterConfig} the wrapped Jetty filter is initialized with. It carries no servlet context, as
 * the wrapped filter reads only init parameters.
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
