package com.enonic.xp.web.impl.serializer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteSource;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

public final class ResponseSerializer
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final WebRequest webRequest;

    private final WebResponse webResponse;

    public ResponseSerializer( final WebRequest webRequest, final WebResponse webResponse )
    {
        this.webRequest = webRequest;
        this.webResponse = webResponse;
    }

    public void serialize( final HttpServletResponse response )
        throws IOException
    {
        if ( response.isCommitted() )
        {
            return;
        }

        response.setStatus( this.webResponse.getStatus().value() );
        response.setContentType( this.webResponse.getContentType().toString() );

        serializeHeaders( response, this.webResponse.getHeaders() );
        serializeCookies( response, this.webResponse.getCookies() );
        serializeBody( response, this.webResponse.getBody() );
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

        if ( body instanceof List )
        {
            serializeBody( response, convertToJson( body ) );
            return;
        }

        if ( body instanceof byte[] )
        {
            serializeBody( response, (byte[]) body );
            return;
        }

        serializeBody( response, body == null ? "" : body.toString() );
    }

    private String convertToJson( final Object value )
    {
        try
        {
            return MAPPER.writeValueAsString( value );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private void serializeBody( final HttpServletResponse response, final ByteSource body )
        throws IOException
    {
        writeToStream( response, body );
    }

    private void serializeBody( final HttpServletResponse response, final byte[] body )
        throws IOException
    {
        writeToStream( response, body );
    }

    private void serializeBody( final HttpServletResponse response, final String body )
        throws IOException
    {
        writeToStream( response, body.getBytes( StandardCharsets.UTF_8 ) );
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

    private void writeToStream( final HttpServletResponse response, final ByteSource data )
        throws IOException
    {
        response.setContentLengthLong( data.size() );

        if ( !isHeadRequest() )
        {
            data.copyTo( response.getOutputStream() );
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
        return this.webRequest.getMethod() == HttpMethod.HEAD;
    }
}
