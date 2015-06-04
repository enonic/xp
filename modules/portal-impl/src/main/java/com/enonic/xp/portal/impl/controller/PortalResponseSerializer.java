package com.enonic.xp.portal.impl.controller;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;

import com.enonic.xp.portal.PortalResponse;

public final class PortalResponseSerializer
{
    private final PortalResponse from;

    public PortalResponseSerializer( final PortalResponse from )
    {
        this.from = from;
    }

    public PortalResponse serialize()
    {
        return PortalResponse.create( from ).
            header( HttpHeaders.CONTENT_TYPE, this.from.getContentType() ).
            body( serializeBody() ).
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
        try
        {
            return new ObjectMapper().writeValueAsString( value );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }
}
