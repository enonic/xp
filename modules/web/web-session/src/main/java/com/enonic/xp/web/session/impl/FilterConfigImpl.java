package com.enonic.xp.web.session.impl;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import com.google.common.collect.Maps;

import static org.apache.ignite.cache.websession.WebSessionFilter.WEB_SES_MAX_RETRIES_ON_FAIL_NAME_PARAM;
import static org.apache.ignite.cache.websession.WebSessionFilter.WEB_SES_NAME_PARAM;

final class FilterConfigImpl
    implements FilterConfig
{
    private final FilterConfig delegate;

    private final Map<String, String> config;

    FilterConfigImpl( final FilterConfig delegate )
    {
        this.delegate = delegate;
        this.config = Maps.newHashMap();
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

    void populate( final WebSessionConfig config )
    {
        this.config.put( WEB_SES_NAME_PARAM, "enonic-xp-ignite-instance" );
        this.config.put( WEB_SES_MAX_RETRIES_ON_FAIL_NAME_PARAM, String.valueOf( config.retries() ) );
    }

    void populate( final String key, final String value )
    {
        this.config.put( key, value );
    }
}
