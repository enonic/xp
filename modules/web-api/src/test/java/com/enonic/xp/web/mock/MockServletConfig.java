package com.enonic.xp.web.mock;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class MockServletConfig
    implements ServletConfig
{
    @Override
    public String getServletName()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletContext getServletContext()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getInitParameter( final String name )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<String> getInitParameterNames()
    {
        throw new UnsupportedOperationException();
    }
}
