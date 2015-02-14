package com.enonic.xp.web.jaxrs.impl;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import com.google.common.collect.Maps;

final class ServletConfigImpl
    implements ServletConfig
{
    private final String name;

    private final ServletContext context;

    private final Map<String, String> params;

    public ServletConfigImpl( final String name, final ServletContext context )
    {
        this.name = name;
        this.context = context;
        this.params = Maps.newHashMap();
    }

    @Override
    public String getServletName()
    {
        return this.name;
    }

    @Override
    public ServletContext getServletContext()
    {
        return this.context;
    }

    @Override
    public String getInitParameter( final String name )
    {
        return this.params.get( name );
    }

    @Override
    public Enumeration<String> getInitParameterNames()
    {
        return Collections.enumeration( this.params.keySet() );
    }

    public void setInitParameter( final String name, final String value )
    {
        this.params.put( name, value );
    }
}
