package com.enonic.xp.web.jmx.impl;

import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import com.google.common.collect.ImmutableMap;

final class JmxServletConfig
    implements ServletConfig
{
    private final ServletConfig real;

    private final ImmutableMap<String, String> config;

    public JmxServletConfig( final ServletConfig real )
    {
        this.real = real;

        final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        builder.put( "mimeType", "application/json" );
        builder.put( "serializeException", "true" );
        builder.put( "includeStackTrace", "false" );
        builder.put( "discoveryEnabled", "false" );
        builder.put( "agentId", "web-jmx" );
        builder.put( "logHandlerClass", JmxLogHandler.class.getName() );

        this.config = builder.build();
    }

    @Override
    public String getServletName()
    {
        return this.real.getServletName();
    }

    @Override
    public ServletContext getServletContext()
    {
        return this.real.getServletContext();
    }

    @Override
    public String getInitParameter( final String key )
    {
        return this.config.get( key );
    }

    @Override
    public Enumeration<String> getInitParameterNames()
    {
        return Collections.enumeration( this.config.keySet() );
    }
}
