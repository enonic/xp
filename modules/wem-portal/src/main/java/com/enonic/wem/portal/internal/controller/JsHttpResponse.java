package com.enonic.wem.portal.internal.controller;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.wem.portal.PortalResponse;

public final class JsHttpResponse
    implements PortalResponse
{
    public final static int STATUS_OK = 200;

    public final static int STATUS_METHOD_NOT_ALLOWED = 405;

    private int status = STATUS_OK;

    private String contentType = "text/plain";

    private Object body;

    private final Map<String, String> headers;

    private boolean postProcess = true;

    public JsHttpResponse()
    {
        this.headers = Maps.newHashMap();
    }

    @Override
    public int getStatus()
    {
        return this.status;
    }

    @Override
    public void setStatus( final int status )
    {
        this.status = status;
    }

    @Override
    public String getContentType()
    {
        return this.contentType;
    }

    @Override
    public void setContentType( final String contentType )
    {
        this.contentType = contentType;
    }

    @Override
    public Object getBody()
    {
        return this.body;
    }

    @Override
    public void setBody( final Object body )
    {
        this.body = body;
    }

    @Override
    public Map<String, String> getHeaders()
    {
        return this.headers;
    }

    @Override
    public void header( final String name, final String value )
    {
        this.headers.put( name, value );
    }

    @Override
    public boolean isPostProcess()
    {
        return postProcess;
    }

    @Override
    public void setPostProcess( final boolean postProcess )
    {
        this.postProcess = postProcess;
    }
}
