package com.enonic.wem.portal.controller;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import static org.junit.Assert.*;

public class JsHttpResponseSerializerTest
{
    private JsHttpResponse response;

    private JsHttpResponseSerializer serializer;

    @Before
    public void setup()
    {
        this.response = new JsHttpResponse();
        this.serializer = new JsHttpResponseSerializer( this.response );
    }

    @Test
    public void testError()
    {
        this.response.setStatus( JsHttpResponse.STATUS_METHOD_NOT_ALLOWED );
        final Response result = this.serializer.serialize();

        assertNotNull( result );
        assertEquals( JsHttpResponse.STATUS_METHOD_NOT_ALLOWED, result.getStatus() );
        assertNull( result.getEntity() );
    }

    @Test
    public void testJsonResult()
    {
        this.response.setContentType( "application/json" );
        this.response.setBody( ImmutableMap.of( "key", "value" ) );
        final Response result = this.serializer.serialize();

        assertNotNull( result );
        assertEquals( JsHttpResponse.STATUS_OK, result.getStatus() );
        assertEquals( MediaType.APPLICATION_JSON_TYPE, result.getMetadata().getFirst( "Content-Type" ) );
        assertEquals( "{\"key\":\"value\"}", result.getEntity() );
    }

    @Test
    public void testStringResult()
    {
        this.response.setContentType( "text/plain" );
        this.response.setBody( "Hello world!" );
        final Response result = this.serializer.serialize();

        assertNotNull( result );
        assertEquals( JsHttpResponse.STATUS_OK, result.getStatus() );
        assertEquals( MediaType.TEXT_PLAIN_TYPE, result.getMetadata().getFirst( "Content-Type" ) );
        assertEquals( "Hello world!", result.getEntity() );
    }

    @Test
    public void testBytesResult()
    {
        final byte[] bytes = "bytes".getBytes();
        this.response.setContentType( "application/octet-stream" );
        this.response.setBody( bytes );
        final Response result = this.serializer.serialize();

        assertNotNull( result );
        assertEquals( JsHttpResponse.STATUS_OK, result.getStatus() );
        assertEquals( MediaType.APPLICATION_OCTET_STREAM_TYPE, result.getMetadata().getFirst( "Content-Type" ) );
        assertSame( bytes, result.getEntity() );
    }

    @Test
    public void testObjectResult()
    {
        this.response.setContentType( "text/plain" );
        this.response.setBody( 11 );
        final Response result = this.serializer.serialize();

        assertNotNull( result );
        assertEquals( JsHttpResponse.STATUS_OK, result.getStatus() );
        assertEquals( MediaType.TEXT_PLAIN_TYPE, result.getMetadata().getFirst( "Content-Type" ) );
        assertEquals( "11", result.getEntity() );
    }

    @Test
    public void testHeadersWithResult()
    {
        this.response.setContentType( "text/plain" );
        this.response.setBody( "With headers" );
        this.response.header( "X-MyHeader", "Value" );
        final Response result = this.serializer.serialize();

        assertNotNull( result );
        assertEquals( JsHttpResponse.STATUS_OK, result.getStatus() );
        assertEquals( MediaType.TEXT_PLAIN_TYPE, result.getMetadata().getFirst( "Content-Type" ) );
        assertEquals( "Value", result.getMetadata().getFirst( "X-MyHeader" ) );
        assertEquals( "With headers", result.getEntity() );
    }
}
