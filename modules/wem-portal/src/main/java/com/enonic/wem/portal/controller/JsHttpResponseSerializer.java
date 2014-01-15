package com.enonic.wem.portal.controller;

import java.util.Map;

import javax.ws.rs.core.Response;

import com.google.gson.Gson;

final class JsHttpResponseSerializer
{
    private final JsHttpResponse from;

    private Response.ResponseBuilder builder;

    public JsHttpResponseSerializer( final JsHttpResponse from )
    {
        this.from = from;
    }

    public Response serialize()
    {
        this.builder = Response.status( this.from.getStatus() );
        this.builder.type( this.from.getContentType() );

        serializeBody();
        serializeHeaders();

        return this.builder.build();
    }

    private void serializeBody()
    {
        final Object body = this.from.getBody();
        if ( body != null )
        {
            this.builder.entity( convert( body ) );
        }
    }

    private void serializeHeaders()
    {
        for ( final Map.Entry<String, String> header : this.from.getHeaders().entrySet() )
        {
            this.builder.header( header.getKey(), header.getValue() );
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
