package com.enonic.xp.jaxrs.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.StreamSupport;

import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.client.util.MultiPartContentProvider;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.UnhandledException;

public final class RestRequestBuilder
{
    private final Dispatcher dispatcher;

    private final StringBuilder uri;

    private String baseUri;

    private int numParams = 0;

    private byte[] entity;

    private String entityType;

    public RestRequestBuilder( final Dispatcher dispatcher )
    {
        this.dispatcher = dispatcher;
        this.uri = new StringBuilder();
    }

    public RestRequestBuilder path( final String path )
    {
        if ( path.startsWith( "/" ) )
        {
            return path( path.substring( 1 ) );
        }

        this.uri.append( path );
        return this;
    }

    public RestRequestBuilder baseUri( final String baseUri )
    {
        this.baseUri = baseUri;
        return this;
    }

    public RestRequestBuilder queryParam( final String name, final String value )
        throws Exception
    {
        final String str = name + "=" + URLEncoder.encode( value, StandardCharsets.UTF_8 );
        if ( this.numParams == 0 )
        {
            this.uri.append( "?" );
        }
        else
        {
            this.uri.append( "&" );
        }

        this.uri.append( str );
        this.numParams++;
        return this;
    }

    public RestRequestBuilder entity( final String data, final MediaType type )
    {
        return entity( data.getBytes(), type );
    }

    public RestRequestBuilder entity( final byte[] data, final MediaType type )
    {
        this.entity = data;
        this.entityType = type.toString();
        return this;
    }

    public RestRequestBuilder multipart( final String name, final String fileName, final byte[] data, final MediaType type )
        throws Exception
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        MultiPartContentProvider multiPart = new MultiPartContentProvider();
        multiPart.addFilePart( name, fileName, new BytesContentProvider( data ), null );
        multiPart.close();
        StreamSupport.stream( multiPart.spliterator(), false ).forEachOrdered( bb -> out.writeBytes( bb.array() ) );

        this.entity = out.toByteArray();
        this.entityType = multiPart.getContentType();
        return this;
    }

    public MockRestResponse get()
        throws Exception
    {
        final MockHttpRequest request = baseUri != null
            ? MockHttpRequest.create( "GET", new URI( this.uri.toString() ), new URI( baseUri ) )
            : MockHttpRequest.get( this.uri.toString() );
        return execute( request );
    }

    public MockRestResponse post()
        throws Exception
    {
        final MockHttpRequest request = baseUri != null
            ? MockHttpRequest.create( "POST", new URI( this.uri.toString() ), new URI( baseUri ) )
            : MockHttpRequest.post( this.uri.toString() );
        request.setInputStream( new ByteArrayInputStream( this.entity ) );
        request.header( "Content-Type", this.entityType );
        return execute( request );
    }

    private MockRestResponse execute( final MockHttpRequest request )
        throws Exception
    {
        try
        {
            final MockHttpResponse response = new MockHttpResponse();
            this.dispatcher.invoke( request, response );
            return toResponse( response );
        }
        catch ( final UnhandledException e )
        {
            final Throwable cause = e.getCause();
            if ( cause instanceof Exception )
            {
                throw (Exception) cause;
            }
            else
            {
                throw e;
            }
        }
    }

    private MockRestResponse toResponse( final MockHttpResponse from )
    {
        final MockRestResponse to = new MockRestResponse();
        to.setStatus( from.getStatus() );
        to.setData( from.getOutput() );
        to.setHeaders( from.getOutputHeaders() );
        return to;
    }
}
