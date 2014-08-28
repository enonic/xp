package com.enonic.wem.portal.internal.controller;

import java.util.Map;

import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;

import com.enonic.wem.portal.PortalResponse;
import com.enonic.wem.portal.internal.rendering.RenderResult;

public final class JsHttpResponseSerializer
{
    private final PortalResponse from;

    public JsHttpResponseSerializer( final PortalResponse from )
    {
        this.from = from;
    }

    public RenderResult serialize()
    {
        return RenderResult.newRenderResult().
            status( this.from.getStatus() ).
            type( this.from.getContentType() ).
            headers( this.from.getHeaders() ).
            header( HttpHeaders.CONTENT_TYPE, this.from.getContentType() ).
            entity( serializeBody() ).
            build();
    }

    private Object serializeBody()
    {
        final Object body = this.from.getBody();
        if ( body != null )
        {
            return convert( body );
        }
        else
        {
            return null;
        }
    }

    private Object convert( final Object value )
    {
        if ( value instanceof Map )
        {
            return convertToJson( value );
        }

        if ( value instanceof byte[] )
        {
            return value;
        }

        return value.toString();
    }

    private String convertToJson( final Object value )
    {
        return new Gson().toJson( value );
    }
}
