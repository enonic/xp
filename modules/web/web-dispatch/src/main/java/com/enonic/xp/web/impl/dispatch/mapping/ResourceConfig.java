package com.enonic.xp.web.impl.dispatch.mapping;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import com.google.common.collect.Iterators;

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
        return Iterators.asEnumeration( this.initParams.keySet().iterator() );
    }

    @Override
    public String getServletName()
    {
        return this.name;
    }
}
