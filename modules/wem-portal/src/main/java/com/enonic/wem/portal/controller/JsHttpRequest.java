package com.enonic.wem.portal.controller;

import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.core.HttpRequestContext;

public final class JsHttpRequest
{
    private final HttpRequestContext raw;

    private final MultivaluedMap<String, String> queryParameters;

    private String mode;

    public JsHttpRequest( final HttpRequestContext raw )
    {
        this.raw = raw;
        this.queryParameters = raw.getQueryParameters();
    }

    public String getMethod()
    {
        return this.raw.getMethod();
    }

    public MultivaluedMap<String, String> getParams()
    {
        return queryParameters;
    }

    public String getMode()
    {
        return mode;
    }

    public void setMode( final String mode )
    {
        this.mode = mode;
    }
}
