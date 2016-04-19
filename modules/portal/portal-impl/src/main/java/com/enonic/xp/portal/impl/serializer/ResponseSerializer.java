package com.enonic.xp.portal.impl.serializer;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteSource;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.web.HttpMethod;

public final class ResponseSerializer
{
    private final PortalRequest portalRequest;

    private final PortalResponse portalResponse;

    public ResponseSerializer( final PortalRequest portalRequest, final PortalResponse portalResponse )
    {
        this.portalRequest = portalRequest;
        this.portalResponse = portalResponse;
    }

    public void serialize( final HttpServletResponse response )
        throws IOException
    {
        if ( response.isCommitted() )
        {
            return;
        }

        response.setStatus( this.portalResponse.getStatus().value() );
        response.setContentType( this.portalResponse.getContentType().toString() );

        serializeHeaders( response, this.portalResponse.getHeaders() );
        serializeCookies( response, this.portalResponse.getCookies() );
        serializeBody( response, this.portalResponse.getBody() );
    }

    private void serializeCookies( final HttpServletResponse response, final ImmutableList<Cookie> cookies )
    {
        for ( final Cookie cookie : cookies )
        {
            response.addCookie( cookie );
        }
    }

    private void serializeBody( final HttpServletResponse response, final Object body )
        throws IOException
    {
        if ( body instanceof Resource )
        {
            serializeBody( response, (Resource) body );
            return;
        }

        if ( body instanceof ByteSource )
        {
            serializeBody( response, (ByteSource) body );
            return;
        }

        if ( body instanceof Map )
        {
            serializeBody( response, convertToJson( body ) );
            return;
        }

        if ( body instanceof byte[] )
        {
            serializeBody( response, (byte[]) body );
            return;
        }

        if ( body != null )
        {
            serializeBody( response, body.toString() );
        }
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

    private void serializeBody( final HttpServletResponse response, final ByteSource body )
        throws IOException
    {
        writeToStream( response, body.read() );
    }

    private void serializeBody( final HttpServletResponse response, final byte[] body )
        throws IOException
    {
        writeToStream( response, body );
    }

    private void serializeBody( final HttpServletResponse response, final String body )
        throws IOException
    {
        writeToStream( response, body.getBytes( Charsets.UTF_8 ) );
    }

    private void writeToStream( final HttpServletResponse response, final byte[] data )
        throws IOException
    {
        response.setContentLength( data.length );

        if ( !isHeadRequest() )
        {
            response.getOutputStream().write( data );
        }
    }

    private void serializeBody( final HttpServletResponse response, final Resource body )
        throws IOException
    {
        writeToStream( response, body.readBytes() );
    }

    private void serializeHeaders( final HttpServletResponse response, final Map<String, String> headers )
    {
        for ( final Map.Entry<String, String> entry : headers.entrySet() )
        {
            response.setHeader( entry.getKey(), entry.getValue() );
        }
    }

    private boolean isHeadRequest()
    {
        return this.portalRequest.getMethod() == HttpMethod.HEAD;
    }
}
