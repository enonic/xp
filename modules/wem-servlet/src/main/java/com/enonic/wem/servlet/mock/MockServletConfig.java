package com.enonic.wem.servlet.mock;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import com.google.common.collect.Maps;

// https://github.com/spring-projects/spring-framework/blob/master/spring-test/src/main/java/org/springframework/mock/web/MockServletConfig.java
public class MockServletConfig
    implements ServletConfig
{
    private ServletContext servletContext;

    private String servletName;

    private final Map<String, String> initParameters;

    public MockServletConfig()
    {
        this.servletName = "";
        this.servletContext = new MockServletContext();
        this.initParameters = Maps.newHashMap();
    }

    @Override
    public String getServletName()
    {
        return this.servletName;
    }

    @Override
    public ServletContext getServletContext()
    {
        return this.servletContext;
    }

    @Override
    public String getInitParameter( final String name )
    {
        return this.initParameters.get( name );
    }

    @Override
    public Enumeration<String> getInitParameterNames()
    {
        return Collections.enumeration( this.initParameters.keySet() );
    }

    public void setServletContext( final ServletContext servletContext )
    {
        this.servletContext = servletContext;
    }

    public void setServletName( final String servletName )
    {
        this.servletName = servletName;
    }

    public void addInitParameter( final String name, final String value )
    {
        this.initParameters.put( name, value );
    }
}
