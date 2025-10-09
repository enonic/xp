package com.enonic.xp.web.impl.serializer;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;

import com.google.common.io.Resources;
import com.google.common.net.MediaType;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class ResponseSerializerTest
{
    @Test
    public void serializeBodyString()
        throws Exception
    {
        final WebRequest req = new WebRequest();
        req.setMethod( HttpMethod.GET );
        final String string_body = "String body";

        final WebResponse resp = WebResponse.create().
            status( HttpStatus.ACCEPTED ).
            contentType( MediaType.PLAIN_TEXT_UTF_8 ).
            header( "header-test", "header-value" ).
            cookie( new Cookie( "cookie-name", "cookie-value" ) ).
            body( string_body ).
            build();
        final ResponseSerializer serializer = new ResponseSerializer( req, resp );

        final HttpServletResponse httpResponse = mock( HttpServletResponse.class );
        final ServletOutputStream servletOutputStream = mock( ServletOutputStream.class );
        when( httpResponse.getCharacterEncoding() ).thenReturn( "utf-8" );
        when( httpResponse.getOutputStream() ).thenReturn( servletOutputStream );

        serializer.serialize( httpResponse );

        verify( httpResponse ).setHeader( "header-test", "header-value" );
        verify( httpResponse ).setStatus( 202 );
        verify( httpResponse ).setContentType( "text/plain; charset=utf-8" );
        verify( httpResponse ).setContentLengthLong( 11 );
        verify( servletOutputStream ).write( string_body.getBytes( StandardCharsets.UTF_8 ), 0, 11 );
    }

    @Test
    public void serializeBodyBytes()
        throws Exception
    {
        final WebRequest req = new WebRequest();
        req.setMethod( HttpMethod.GET );
        final byte[] body_bytes = "String body".getBytes( StandardCharsets.UTF_8 );
        final WebResponse resp = WebResponse.create().
            status( HttpStatus.ACCEPTED ).
            contentType( MediaType.PLAIN_TEXT_UTF_8 ).
            header( "header-test", "header-value" ).
            cookie( new Cookie( "cookie-name", "cookie-value" ) ).
            body( body_bytes ).
            build();
        final ResponseSerializer serializer = new ResponseSerializer( req, resp );

        final HttpServletResponse httpResponse = mock( HttpServletResponse.class );
        final ServletOutputStream servletOutputStream = mock( ServletOutputStream.class );
        when( httpResponse.getOutputStream() ).thenReturn( servletOutputStream );

        serializer.serialize( httpResponse );

        verify( httpResponse ).setHeader( "header-test", "header-value" );
        verify( httpResponse ).setStatus( 202 );
        verify( httpResponse ).setContentType( "text/plain; charset=utf-8" );
        verify( httpResponse ).setContentLengthLong( 11 );
        verify( servletOutputStream ).write( body_bytes, 0 ,11 );
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

        final HttpServletResponse httpResponse = mock( HttpServletResponse.class );
        final ServletOutputStream servletOutputStream = mock( ServletOutputStream.class );
        when( httpResponse.getOutputStream() ).thenReturn( servletOutputStream );

        serializer.serialize( httpResponse );

        verify( httpResponse ).setHeader( "header-test", "header-value" );
        verify( httpResponse ).setStatus( 202 );
        verify( httpResponse ).setContentType( "text/plain; charset=utf-8" );
        verify( httpResponse, times( 0 ) ).setContentLength( anyInt() );
        verify( servletOutputStream ).write( any( byte[].class ), eq( 0 ), eq( 11 ) );
    }

    @Test
    public void serializeBodyResource()
        throws Exception
    {
        final ResourceKey resourceKey = ResourceKey.from( "myapp:/cms/test/view/body_file.txt" );
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

        final HttpServletResponse httpResponse = mock( HttpServletResponse.class );
        final ServletOutputStream servletOutputStream = mock( ServletOutputStream.class );
        when( httpResponse.getOutputStream() ).thenReturn( servletOutputStream );

        serializer.serialize( httpResponse );

        verify( httpResponse ).setHeader( "header-test", "header-value" );
        verify( httpResponse ).setStatus( 202 );
        verify( httpResponse ).setContentType( "text/plain; charset=utf-8" );
        verify( httpResponse ).setContentLengthLong( 11 );
        verify( servletOutputStream ).write( any( byte[].class ), eq( 0 ) , eq( 11 ) );
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

        final HttpServletResponse httpResponse = mock( HttpServletResponse.class );
        final ServletOutputStream servletOutputStream = mock( ServletOutputStream.class );
        when( httpResponse.getCharacterEncoding() ).thenReturn( "utf-8" );
        when( httpResponse.getOutputStream() ).thenReturn( servletOutputStream );

        serializer.serialize( httpResponse );

        verify( httpResponse ).setHeader( "header-test", "header-value" );
        verify( httpResponse ).setStatus( 202 );
        verify( httpResponse ).setContentType( "text/plain; charset=utf-8" );
        verify( httpResponse ).setContentLengthLong( 39 );
        verify( servletOutputStream ).write( any( byte[].class ), eq( 0 ), eq( 39 ) );
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

        final HttpServletResponse httpResponse = mock( HttpServletResponse.class );
        when( httpResponse.getCharacterEncoding() ).thenReturn( "utf-8" );

        serializer.serialize( httpResponse );

        verify( httpResponse ).setHeader( "header-test", "header-value" );
        verify( httpResponse ).setStatus( 202 );
        verify( httpResponse ).setContentType( "text/plain; charset=utf-8" );
        verify( httpResponse ).setContentLengthLong( 11 );
        verify( httpResponse, times( 0 ) ).getOutputStream();
        verify( httpResponse, times( 1 ) ).flushBuffer();
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

        final HttpServletResponse httpResponse = mock( HttpServletResponse.class );
        when( httpResponse.isCommitted() ).thenReturn( true );

        serializer.serialize( httpResponse );

        verify( httpResponse ).isCommitted();
        verifyNoMoreInteractions( httpResponse );
    }
}
