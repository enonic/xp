package com.enonic.wem.portal.controller;

import com.sun.jersey.api.core.HttpRequestContext;

public final class JsHttpRequest
{
    private final HttpRequestContext raw;

    public JsHttpRequest( final HttpRequestContext raw )
    {
        this.raw = raw;
    }

    public String getMethod()
    {
        return this.raw.getMethod();
    }
}
