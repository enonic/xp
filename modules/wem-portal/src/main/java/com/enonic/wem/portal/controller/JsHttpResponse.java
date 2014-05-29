package com.enonic.wem.portal.controller;

import java.util.Map;

import com.google.common.collect.Maps;

public final class JsHttpResponse
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

    public int getStatus()
    {
        return this.status;
    }

    public void setStatus( final int status )
    {
        this.status = status;
    }

    public String getContentType()
    {
        return this.contentType;
    }

    public void setContentType( final String contentType )
    {
        this.contentType = contentType;
    }

    public Object getBody()
    {
        return this.body;
    }

    public void setBody( final Object body )
    {
        this.body = body;
    }

    public Map<String, String> getHeaders()
    {
        return this.headers;
    }

    public void header( final String name, final String value )
    {
        this.headers.put( name, value );
    }

    public boolean isPostProcess()
    {
        return postProcess;
    }

    public void setPostProcess( final boolean postProcess )
    {
        this.postProcess = postProcess;
    }
}
