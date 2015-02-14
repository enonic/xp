package com.enonic.xp.portal.impl.controller;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.rendering.RenderResult;

import static com.google.common.net.MediaType.JSON_UTF_8;
import static com.google.common.net.MediaType.OCTET_STREAM;
import static com.google.common.net.MediaType.PLAIN_TEXT_UTF_8;
import static org.junit.Assert.*;

public class PortalResponseSerializerTest
{
    private PortalResponse response;

    private PortalResponseSerializer serializer;

    @Before
    public void setup()
    {
        this.response = new PortalResponse();
        this.serializer = new PortalResponseSerializer( this.response );
    }

    @Test
    public void testError()
    {
        this.response.setStatus( PortalResponse.STATUS_METHOD_NOT_ALLOWED );
        final RenderResult result = this.serializer.serialize();

        assertNotNull( result );
        assertEquals( PortalResponse.STATUS_METHOD_NOT_ALLOWED, result.getStatus() );
        assertNull( result.getEntity() );
    }

    @Test
    public void testJsonResult()
    {
        this.response.setContentType( "application/json" );
        this.response.setBody( ImmutableMap.of( "key", "value" ) );
        final RenderResult result = this.serializer.serialize();

        assertNotNull( result );
        assertEquals( PortalResponse.STATUS_OK, result.getStatus() );
        assertTrue( JSON_UTF_8.withoutParameters().equals( MediaType.parse( result.getHeaders().get( "content-type" ) ) ) );
        assertEquals( "{\"key\":\"value\"}", result.getEntity() );
    }

    @Test
    public void testStringResult()
    {
        this.response.setContentType( "text/plain" );
        this.response.setBody( "Hello world!" );
        final RenderResult result = this.serializer.serialize();

        assertNotNull( result );
        assertEquals( PortalResponse.STATUS_OK, result.getStatus() );
        assertTrue( PLAIN_TEXT_UTF_8.withoutParameters().equals( MediaType.parse( result.getHeaders().get( "Content-Type" ) ) ) );
        assertEquals( "Hello world!", result.getEntity() );
    }

    @Test
    public void testBytesResult()
    {
        final byte[] bytes = "bytes".getBytes();
        this.response.setContentType( "application/octet-stream" );
        this.response.setBody( bytes );
        final RenderResult result = this.serializer.serialize();

        assertNotNull( result );
        assertEquals( PortalResponse.STATUS_OK, result.getStatus() );
        assertTrue( OCTET_STREAM.equals( MediaType.parse( result.getHeaders().get( "Content-Type" ) ) ) );
        assertSame( bytes, result.getEntity() );
    }

    @Test
    public void testObjectResult()
    {
        this.response.setContentType( "text/plain" );
        this.response.setBody( 11 );
        final RenderResult result = this.serializer.serialize();

        assertNotNull( result );
        assertEquals( PortalResponse.STATUS_OK, result.getStatus() );
        assertTrue( PLAIN_TEXT_UTF_8.withoutParameters().equals( MediaType.parse( result.getHeaders().get( "Content-Type" ) ) ) );
        assertEquals( "11", result.getEntity() );
    }

    @Test
    public void testHeadersWithResult()
    {
        this.response.setContentType( "text/plain" );
        this.response.setBody( "With headers" );
        this.response.addHeader( "X-myheader", "Value" );
        final RenderResult result = this.serializer.serialize();

        assertNotNull( result );
        assertEquals( PortalResponse.STATUS_OK, result.getStatus() );
        assertTrue( PLAIN_TEXT_UTF_8.withoutParameters().equals( MediaType.parse( result.getHeaders().get( "Content-Type" ) ) ) );
        assertEquals( "Value", result.getHeaders().get( "X-MyHeader" ) );
        assertEquals( "With headers", result.getEntity() );
    }
}
