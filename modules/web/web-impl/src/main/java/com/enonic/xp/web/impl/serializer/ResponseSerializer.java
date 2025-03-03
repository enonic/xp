package com.enonic.xp.web.impl.serializer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

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

        this.webResponse.getHeaders().forEach( response::setHeader );
        this.webResponse.getCookies().forEach( response::addCookie );
        serializeBody( response, this.webResponse.getBody() );
    }

    private void serializeBody( final HttpServletResponse response, final Object body )
        throws IOException
    {
        if ( body instanceof ByteSource )
        {
            writeToStream( response, (ByteSource) body );
        }
        else if ( body instanceof Resource )
        {
            writeToStream( response, ( (Resource) body ).getBytes() );
        }
        else if ( body instanceof byte[] )
        {
            writeBytesToStream( response, (byte[]) body );
        }
        else if ( body instanceof CharSequence )
        {
            writeStringToStream( response, (CharSequence) body );
        }
        else if ( body instanceof Map || body instanceof List )
        {
            writeStringToStream( response, MAPPER.writeValueAsString( body ) );
        }
        else if ( body != null )
        {
            writeStringToStream( response, body.toString() );
        }
    }

    private void writeStringToStream( final HttpServletResponse response, final CharSequence data )
        throws IOException
    {
        // Make sure the content-length is known by reading string into byte array
        writeBytesToStream( response, CharSource.wrap( data ).asByteSource( Charset.forName( response.getCharacterEncoding() ) ).read() );
    }

    private void writeBytesToStream( final HttpServletResponse response, final byte[] data )
        throws IOException
    {
        writeToStream( response, ByteSource.wrap( data ) );
    }

    private void writeToStream( final HttpServletResponse response, final ByteSource data )
        throws IOException
    {
        data.sizeIfKnown().toJavaUtil().ifPresent( response::setContentLengthLong );

        if ( this.webRequest.getMethod() != HttpMethod.HEAD )
        {
            data.copyTo( response.getOutputStream() );
        }
        else
        {
            // Make sure Jetty does not try to calculate content-length response header for HEAD request
            response.flushBuffer();
        }
    }

}
