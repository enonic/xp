package com.enonic.xp.web.mock;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

public class MockHttpSession
    implements HttpSession
{
    private final ServletContext servletContext;

    public MockHttpSession( final ServletContext servletContext )
    {
        this.servletContext = servletContext;
    }

    @Override
    public long getCreationTime()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getId()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getLastAccessedTime()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletContext getServletContext()
    {
        return this.servletContext;
    }

    @Override
    public void setMaxInactiveInterval( final int interval )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMaxInactiveInterval()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpSessionContext getSessionContext()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getAttribute( final String name )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getValue( final String name )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<String> getAttributeNames()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getValueNames()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttribute( final String name, final Object value )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putValue( final String name, final Object value )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAttribute( final String name )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeValue( final String name )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void invalidate()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isNew()
    {
        throw new UnsupportedOperationException();
    }
}
