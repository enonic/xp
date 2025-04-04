package com.enonic.xp.jaxrs.impl;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;

final class ServletConfigImpl
    implements ServletConfig
{
    private final String name;

    private final ServletContext context;

    private final Map<String, String> params;

    ServletConfigImpl( final String name, final ServletContext context )
    {
        this.name = name;
        this.context = context;
        this.params = new HashMap<>();
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

    void setInitParameter( final String name, final String value )
    {
        this.params.put( name, value );
    }
}
