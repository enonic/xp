package com.enonic.wem.portal.controller;

import java.util.Map;

import javax.ws.rs.core.Response;

final class JsHttpResponseSerializer
{
    private final JsHttpResponse from;

    public JsHttpResponseSerializer( final JsHttpResponse from )
    {
        this.from = from;
    }

    public Response serialize()
    {
        final Response.ResponseBuilder builder = Response.status( this.from.getStatus() );
        builder.type( this.from.getContentType() );

        serializeBody( builder );
        serializeHeaders( builder );

        return builder.build();
    }

    private void serializeBody( final Response.ResponseBuilder builder )
    {
        final Object body = this.from.getBody();
        if ( body != null )
        {
            builder.entity( convert( body ) );
        }
    }

    private void serializeHeaders( final Response.ResponseBuilder builder )
    {
        for ( final Map.Entry<String, String> header : this.from.getHeaders().entrySet() )
        {
            builder.header( header.getKey(), header.getValue() );
        }
    }

    private Object convert( final Object value )
    {
        return new JsObjectConverter().convert( value );
    }
}
