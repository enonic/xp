package com.enonic.xp.web.impl.serializer;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import com.google.common.io.Resources;
import com.google.common.net.MediaType;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.junit.Assert.*;

public class ResponseSerializerTest
{
    @Test
    public void serializeBodyString()
        throws Exception
    {
        final WebRequest req = new WebRequest();
        req.setMethod( HttpMethod.GET );
        final WebResponse resp = WebResponse.create().
            status( HttpStatus.ACCEPTED ).
            contentType( MediaType.PLAIN_TEXT_UTF_8 ).
            header( "header-test", "header-value" ).
            cookie( new Cookie( "cookie-name", "cookie-value" ) ).
            body( "String body" ).
            build();
        final ResponseSerializer serializer = new ResponseSerializer( req, resp );

        final MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        serializer.serialize( httpResponse );

        assertEquals( 202, httpResponse.getStatus() );
        assertEquals( "header-value", httpResponse.getHeader( "header-test" ) );
        assertEquals( "text/plain; charset=utf-8", httpResponse.getContentType() );
        assertEquals( "String body", httpResponse.getContentAsString() );
    }

    @Test
    public void serializeBodyBytes()
        throws Exception
    {
        final WebRequest req = new WebRequest();
        req.setMethod( HttpMethod.GET );
        final WebResponse resp = WebResponse.create().
            status( HttpStatus.ACCEPTED ).
            contentType( MediaType.PLAIN_TEXT_UTF_8 ).
            header( "header-test", "header-value" ).
            cookie( new Cookie( "cookie-name", "cookie-value" ) ).
            body( "String body".getBytes( StandardCharsets.UTF_8 ) ).
            build();
        final ResponseSerializer serializer = new ResponseSerializer( req, resp );

        final MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        serializer.serialize( httpResponse );

        assertEquals( 202, httpResponse.getStatus() );
        assertEquals( "header-value", httpResponse.getHeader( "header-test" ) );
        assertEquals( "text/plain; charset=utf-8", httpResponse.getContentType() );
        assertArrayEquals( "String body".getBytes( StandardCharsets.UTF_8 ), httpResponse.getContentAsByteArray() );
    }

    @Test
    public void serializeBodyByteSource()
        throws Exception
    {
        final WebRequest req = new WebRequest();
        req.setMethod( HttpMethod.GET );
        final WebResponse resp = WebResponse.create().
            status( HttpStatus.ACCEPTED ).
            contentType( MediaType.PLAIN_TEXT_UTF_8 ).
            header( "header-test", "header-value" ).
            cookie( new Cookie( "cookie-name", "cookie-value" ) ).
            body( Resources.asByteSource( ResponseSerializerTest.class.getResource( "body_file.txt" ) ) ).
            build();
        final ResponseSerializer serializer = new ResponseSerializer( req, resp );

        final MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        serializer.serialize( httpResponse );

        assertEquals( 202, httpResponse.getStatus() );
        assertEquals( "header-value", httpResponse.getHeader( "header-test" ) );
        assertEquals( "text/plain; charset=utf-8", httpResponse.getContentType() );
        assertArrayEquals( "String body".getBytes( StandardCharsets.UTF_8 ), httpResponse.getContentAsByteArray() );
    }

    @Test
    public void serializeBodyResource()
        throws Exception
    {
        final ResourceKey resourceKey = ResourceKey.from( "myapp:/site/test/view/body_file.txt" );
        final URL resourceUrl = ResponseSerializerTest.class.getResource( "body_file.txt" );

        final WebRequest req = new WebRequest();
        req.setMethod( HttpMethod.GET );
        final WebResponse resp = WebResponse.create().
            status( HttpStatus.ACCEPTED ).
            contentType( MediaType.PLAIN_TEXT_UTF_8 ).
            header( "header-test", "header-value" ).
            cookie( new Cookie( "cookie-name", "cookie-value" ) ).
            body( new UrlResource( resourceKey, resourceUrl ) ).
            build();
        final ResponseSerializer serializer = new ResponseSerializer( req, resp );

        final MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        serializer.serialize( httpResponse );

        assertEquals( 202, httpResponse.getStatus() );
        assertEquals( "header-value", httpResponse.getHeader( "header-test" ) );
        assertEquals( "text/plain; charset=utf-8", httpResponse.getContentType() );
        assertArrayEquals( "String body".getBytes( StandardCharsets.UTF_8 ), httpResponse.getContentAsByteArray() );
    }

    @Test
    public void serializeBodyJsonMap()
        throws Exception
    {
        final Map<String, Object> jsonObject = new HashMap<>();
        jsonObject.put( "key1", "value1" );
        jsonObject.put( "key2", true );
        jsonObject.put( "key3", 42 );

        final WebRequest req = new WebRequest();
        req.setMethod( HttpMethod.GET );
        final WebResponse resp = WebResponse.create().
            status( HttpStatus.ACCEPTED ).
            contentType( MediaType.PLAIN_TEXT_UTF_8 ).
            header( "header-test", "header-value" ).
            cookie( new Cookie( "cookie-name", "cookie-value" ) ).
            body( jsonObject ).
            build();
        final ResponseSerializer serializer = new ResponseSerializer( req, resp );

        final MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        serializer.serialize( httpResponse );

        assertEquals( 202, httpResponse.getStatus() );
        assertEquals( "header-value", httpResponse.getHeader( "header-test" ) );
        assertEquals( "text/plain; charset=utf-8", httpResponse.getContentType() );
        assertEquals( "{\"key1\":\"value1\",\"key2\":true,\"key3\":42}", httpResponse.getContentAsString() );
    }

    @Test
    public void serializeHeadRequest()
        throws Exception
    {
        final WebRequest req = new WebRequest();
        req.setMethod( HttpMethod.HEAD );
        final WebResponse resp = WebResponse.create().
            status( HttpStatus.ACCEPTED ).
            contentType( MediaType.PLAIN_TEXT_UTF_8 ).
            header( "header-test", "header-value" ).
            cookie( new Cookie( "cookie-name", "cookie-value" ) ).
            body( "String body" ).
            build();
        final ResponseSerializer serializer = new ResponseSerializer( req, resp );

        final MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        serializer.serialize( httpResponse );

        assertEquals( 202, httpResponse.getStatus() );
        assertEquals( "header-value", httpResponse.getHeader( "header-test" ) );
        assertEquals( "text/plain; charset=utf-8", httpResponse.getContentType() );
        assertEquals( "", httpResponse.getContentAsString() );
    }

    @Test
    public void serializeRequestCommitted()
        throws Exception
    {
        final WebRequest req = new WebRequest();
        req.setMethod( HttpMethod.GET );
        final WebResponse resp = WebResponse.create().
            status( HttpStatus.OK ).
            contentType( MediaType.PLAIN_TEXT_UTF_8 ).
            body( "String body" ).
            build();
        final ResponseSerializer serializer = new ResponseSerializer( req, resp );

        final MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        httpResponse.setCommitted( true );
        serializer.serialize( httpResponse );

        assertEquals( 200, httpResponse.getStatus() );
        assertEquals( "", httpResponse.getContentAsString() );
    }
}