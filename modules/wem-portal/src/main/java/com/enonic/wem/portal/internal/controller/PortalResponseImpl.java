package com.enonic.wem.portal.internal.controller;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.wem.portal.PortalResponse;

public final class PortalResponseImpl
    implements PortalResponse
{
    private int status = STATUS_OK;

    private String contentType = "text/plain; charset=utf-8";

    private Object body;

    private final Map<String, String> headers;

    private boolean postProcess = true;

    public PortalResponseImpl()
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
    public void setContentType( String contentType )
    {
        if ( contentType != null )
        {
            if ( contentType.indexOf( "charset" ) < 1 && contentType.startsWith( "text/html" ) )
            {
                contentType += "; charset=utf-8";
            }
        }
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
