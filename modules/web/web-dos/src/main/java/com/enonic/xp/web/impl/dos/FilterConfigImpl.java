package com.enonic.xp.web.impl.dos;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;

final class FilterConfigImpl
    implements FilterConfig
{
    private final FilterConfig delegate;

    private final Map<String, String> config;

    FilterConfigImpl( final FilterConfig delegate )
    {
        this.delegate = delegate;
        this.config = new HashMap<>();
    }

    @Override
    public String getFilterName()
    {
        return this.delegate.getFilterName();
    }

    @Override
    public ServletContext getServletContext()
    {
        return this.delegate.getServletContext();
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
