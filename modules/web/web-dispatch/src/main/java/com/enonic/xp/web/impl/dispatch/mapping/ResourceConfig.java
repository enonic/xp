package com.enonic.xp.web.impl.dispatch.mapping;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;

final class ResourceConfig
    implements FilterConfig, ServletConfig
{
    private final String name;

    private final ServletContext context;

    private final Map<String, String> initParams;

    ResourceConfig( final String name, final ServletContext context, final Map<String, String> initParams )
    {
        this.name = name;
        this.context = context;
        this.initParams = initParams;
    }

    @Override
    public String getFilterName()
    {
        return this.name;
    }

    @Override
    public ServletContext getServletContext()
    {
        return this.context;
    }

    @Override
    public String getInitParameter( String key )
    {
        return this.initParams.get( key );
    }

    @Override
    public Enumeration<String> getInitParameterNames()
    {
        return Collections.enumeration( this.initParams.keySet() );
    }

    @Override
    public String getServletName()
    {
        return this.name;
    }
}
