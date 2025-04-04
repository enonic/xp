package com.enonic.xp.web.filter;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;

public final class FilterConfigInitParametersOverride
    implements FilterConfig
{
    private final FilterConfig delegate;

    private final Map<String, String> config;

    public FilterConfigInitParametersOverride( final FilterConfig delegate, final Map<String, String> config )
    {
        this.delegate = delegate;
        this.config = ImmutableMap.copyOf( config );
    }

    @Override
    public String getFilterName()
    {
        return delegate.getFilterName();
    }

    @Override
    public ServletContext getServletContext()
    {
        return delegate.getServletContext();
    }

    @Override
    public String getInitParameter( final String name )
    {
        return config.get( name );
    }

    @Override
    public Enumeration<String> getInitParameterNames()
    {
        return Collections.enumeration( config.keySet() );
    }
}
