package com.enonic.wem.portal.controller;

import javax.ws.rs.core.MultivaluedMap;

import com.enonic.wem.api.rendering.RenderingMode;

import static com.enonic.wem.api.rendering.RenderingMode.LIVE;

public final class JsHttpRequestBuilder
{
    private final JsHttpRequestImpl request;

    public JsHttpRequestBuilder()
    {
        this.request = new JsHttpRequestImpl();
    }

    public JsHttpRequestBuilder method( final String method )
    {
        this.request.method = method;
        return this;
    }

    public JsHttpRequestBuilder params( final MultivaluedMap<String, String> params )
    {
        this.request.params = params;
        return this;
    }

    public JsHttpRequestBuilder mode( final String mode )
    {
        return mode( RenderingMode.from( mode, LIVE ) );
    }

    public JsHttpRequestBuilder mode( final RenderingMode mode )
    {
        this.request.mode = mode;
        return this;
    }

    public JsHttpRequest build()
    {
        return this.request;
    }
}
