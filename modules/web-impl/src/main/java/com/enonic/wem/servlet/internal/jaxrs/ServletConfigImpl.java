package com.enonic.wem.servlet.internal.jaxrs;

import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

final class ServletConfigImpl
    implements ServletConfig
{
    private final String name;

    private final ServletContext context;

    public ServletConfigImpl( final String name, final ServletContext context )
    {
        this.name = name;
        this.context = context;
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
        return null;
    }

    @Override
    public Enumeration<String> getInitParameterNames()
    {
        return Collections.emptyEnumeration();
    }
}
