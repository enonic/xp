package com.enonic.wem.portal.controller;

import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.wem.api.rendering.RenderingMode;

public final class JsHttpRequest
{
    private String method;

    private final Multimap<String, String> params;

    private RenderingMode mode;

    public JsHttpRequest()
    {
        this.mode = RenderingMode.LIVE;
        this.params = HashMultimap.create();
    }

    public String getMethod()
    {
        return this.method;
    }

    public Multimap<String, String> getParams()
    {
        return this.params;
    }

    public RenderingMode getMode()
    {
        return this.mode;
    }

    public void setMethod( final String method )
    {
        this.method = method;
    }

    public void setMode( final RenderingMode mode )
    {
        this.mode = mode;
    }

    public void setMode( final String mode )
    {
        setMode( RenderingMode.from( mode, RenderingMode.LIVE ) );
    }

    public void addParam( final String name, final String value )
    {
        this.params.put( name, value );
    }

    public void addParams( final Multimap<String, String> params )
    {
        this.params.putAll( params );
    }

    public void addParams( final Map<String, List<String>> params )
    {
        for ( final Map.Entry<String, List<String>> entry : params.entrySet() )
        {
            this.params.putAll( entry.getKey(), entry.getValue() );
        }
    }
}
