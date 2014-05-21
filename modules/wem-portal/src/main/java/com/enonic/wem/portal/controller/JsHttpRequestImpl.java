package com.enonic.wem.portal.controller;

import javax.ws.rs.core.MultivaluedMap;

import com.enonic.wem.api.rendering.RenderingMode;

final class JsHttpRequestImpl
    implements JsHttpRequest
{
    protected String method;

    protected MultivaluedMap<String, String> params;

    protected RenderingMode mode;

    @Override
    public String getMethod()
    {
        return this.method;
    }

    @Override
    public MultivaluedMap<String, String> getParams()
    {
        return this.params;
    }

    @Override
    public RenderingMode getMode()
    {
        return this.mode;
    }
}
