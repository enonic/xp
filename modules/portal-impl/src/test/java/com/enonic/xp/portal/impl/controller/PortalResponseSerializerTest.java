package com.enonic.xp.portal.impl.controller;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;

import com.enonic.xp.portal.PortalResponse;

import static com.google.common.net.MediaType.JSON_UTF_8;
import static com.google.common.net.MediaType.OCTET_STREAM;
import static com.google.common.net.MediaType.PLAIN_TEXT_UTF_8;
import static org.junit.Assert.*;

public class PortalResponseSerializerTest
{
    private PortalResponse.Builder responseBuilder;

    private PortalResponseSerializer serializer;

    @Before
    public void setup()
    {
        this.responseBuilder = PortalResponse.create();
    }

    @Test
    public void testError()
    {
        this.responseBuilder.status( PortalResponse.STATUS_METHOD_NOT_ALLOWED );
        this.serializer = new PortalResponseSerializer( responseBuilder.build() );
        final PortalResponse result = this.serializer.serialize();

        assertNotNull( result );
        assertEquals( PortalResponse.STATUS_METHOD_NOT_ALLOWED, result.getStatus() );
        assertNull( result.getBody() );
    }

    @Test
    public void testJsonResult()
    {
        this.responseBuilder.contentType( "application/json" ).body( ImmutableMap.of( "key", "value" ) );
        this.serializer = new PortalResponseSerializer( responseBuilder.build() );
        final PortalResponse result = this.serializer.serialize();

        assertNotNull( result );
        assertEquals( PortalResponse.STATUS_OK, result.getStatus() );
        assertTrue( JSON_UTF_8.withoutParameters().equals( MediaType.parse( result.getHeaders().get( "content-type" ) ) ) );
        assertEquals( "{\"key\":\"value\"}", result.getBody() );
    }

    @Test
    public void testStringResult()
    {
        this.responseBuilder.contentType( "text/plain" ).body( "Hello world!" );
        this.serializer = new PortalResponseSerializer( responseBuilder.build() );
        final PortalResponse result = this.serializer.serialize();

        assertNotNull( result );
        assertEquals( PortalResponse.STATUS_OK, result.getStatus() );
        assertTrue( PLAIN_TEXT_UTF_8.withoutParameters().equals( MediaType.parse( result.getHeaders().get( "Content-Type" ) ) ) );
        assertEquals( "Hello world!", result.getBody() );
    }

    @Test
    public void testBytesResult()
    {
        final byte[] bytes = "bytes".getBytes();
        this.responseBuilder.contentType( "application/octet-stream" ).body( bytes );
        this.serializer = new PortalResponseSerializer( responseBuilder.build() );
        final PortalResponse result = this.serializer.serialize();

        assertNotNull( result );
        assertEquals( PortalResponse.STATUS_OK, result.getStatus() );
        assertTrue( OCTET_STREAM.equals( MediaType.parse( result.getHeaders().get( "Content-Type" ) ) ) );
        assertSame( bytes, result.getBody() );
    }

    @Test
    public void testObjectResult()
    {
        this.responseBuilder.contentType( "text/plain" ).body( 11 );
        this.serializer = new PortalResponseSerializer( responseBuilder.build() );
        final PortalResponse result = this.serializer.serialize();

        assertNotNull( result );
        assertEquals( PortalResponse.STATUS_OK, result.getStatus() );
        assertTrue( PLAIN_TEXT_UTF_8.withoutParameters().equals( MediaType.parse( result.getHeaders().get( "Content-Type" ) ) ) );
        assertEquals( "11", result.getBody() );
    }

    @Test
    public void testHeadersWithResult()
    {
        this.responseBuilder.contentType( "text/plain" ).body( "With headers" ).header( "X-myheader", "Value" );
        this.serializer = new PortalResponseSerializer( responseBuilder.build() );
        final PortalResponse result = this.serializer.serialize();

        assertNotNull( result );
        assertEquals( PortalResponse.STATUS_OK, result.getStatus() );
        assertTrue( PLAIN_TEXT_UTF_8.withoutParameters().equals( MediaType.parse( result.getHeaders().get( "Content-Type" ) ) ) );
        assertEquals( "Value", result.getHeaders().get( "X-MyHeader" ) );
        assertEquals( "With headers", result.getBody() );
    }
}
