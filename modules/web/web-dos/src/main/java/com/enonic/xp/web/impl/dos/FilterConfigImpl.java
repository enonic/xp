package com.enonic.xp.web.impl.dos;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;

/**
 * The config the wrapped Jetty filter is initialized with. XP does not initialize filters any more, so there
 * is no container-provided config to build on: the name is ours and the context comes from the request that
 * triggers initialization.
 */
final class FilterConfigImpl
    implements FilterConfig
{
    private final String name;

    private final ServletContext context;

    private final Map<String, String> config;

    FilterConfigImpl( final String name, final ServletContext context )
    {
        this.name = name;
        this.context = context;
        this.config = new HashMap<>();
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
    public String getInitParameter( final String name )
    {
        return this.config.get( name );
    }

    @Override
    public Enumeration<String> getInitParameterNames()
    {
        return Collections.enumeration( this.config.keySet() );
    }

    public void populate( final DosFilterConfig config )
    {
        this.config.put( "maxRequestsPerSec", String.valueOf( config.maxRequestsPerSec() ) );
        this.config.put( "delayMs", String.valueOf( config.delayMs() ) );
        this.config.put( "maxWaitMs", String.valueOf( config.maxWaitMs() ) );
        this.config.put( "throttledRequests", String.valueOf( config.throttledRequests() ) );
        this.config.put( "throttleMs", String.valueOf( config.throttleMs() ) );
        this.config.put( "maxRequestMs", String.valueOf( config.maxRequestMs() ) );
        this.config.put( "maxIdleTrackerMs", String.valueOf( config.maxIdleTrackerMs() ) );
        this.config.put( "insertHeaders", String.valueOf( config.insertHeaders() ) );
        this.config.put( "trackSessions", String.valueOf( config.trackSessions() ) );
        this.config.put( "remotePort", String.valueOf( config.remotePort() ) );
        this.config.put( "ipWhitelist", config.ipWhitelist() );
    }
}
