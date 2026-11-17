package com.enonic.xp.web.impl.serializer;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InOrder;

import com.google.common.io.Resources;
import com.google.common.net.MediaType;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.ServletResponseBody;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ResponseSerializerTest
{
    @Test
    void serializeServletResponseBody()
        throws Exception
    {
        final var rawRequest = mock( HttpServletRequest.class );
        final var httpResponse = mock( HttpServletResponse.class );
        final var outputStream = mock( ServletOutputStream.class );
        when( httpResponse.getOutputStream() ).thenReturn( outputStream );
        final var req = new WebRequest();
        req.setRawRequest( rawRequest );
        req.getContentSecurityPolicy().add( "script-src", "'self'" );
        final var cookie = new Cookie( "cookie-name", "cookie-value" );
        final byte[] content = "SDK content".getBytes( StandardCharsets.UTF_8 );
        final ServletResponseBody body = new ServletResponseBody()
        {
            @Override
            public void write( final HttpServletRequest request, final HttpServletResponse response )
                throws IOException
            {
                assertThat( request ).isSameAs( rawRequest );
                assertThat( response ).isSameAs( httpResponse );
                response.setStatus( 201 );
                response.setContentType( "application/json" );
                response.getOutputStream().write( content );
            }

            @Override
            public String toString()
            {
                throw new AssertionError( "ServletResponseBody must be invoked" );
            }
        };
        final var resp = WebResponse.create().
            status( HttpStatus.ACCEPTED ).
            contentType( MediaType.PLAIN_TEXT_UTF_8 ).
            header( "header-test", "header-value" ).
            cookie( cookie ).
            body( body ).
            build();

        new ResponseSerializer( req, resp ).serialize( httpResponse );

        final var order = inOrder( httpResponse, outputStream );
        order.verify( httpResponse ).isCommitted();
        order.verify( httpResponse ).setStatus( 202 );
        order.verify( httpResponse ).setContentType( "text/plain; charset=utf-8" );
        order.verify( httpResponse ).setHeader( "header-test", "header-value" );
        order.verify( httpResponse ).setHeader( "Content-Security-Policy", "script-src 'self'" );
        order.verify( httpResponse ).addCookie( cookie );
        order.verify( httpResponse ).setStatus( 201 );
        order.verify( httpResponse ).setContentType( "application/json" );
        order.verify( httpResponse ).getOutputStream();
        order.verify( outputStream ).write( content );
        verify( httpResponse, never() ).setContentLength( anyInt() );
        verify( httpResponse, never() ).setContentLengthLong( anyLong() );
        verifyNoMoreInteractions( httpResponse, outputStream );
        verifyNoInteractions( rawRequest );
    }

    @Test
    void serializeServletResponseBodyPropagatesException()
    {
        final var req = new WebRequest();
        req.setRawRequest( mock( HttpServletRequest.class ) );
        final var failure = new IOException( "Transport failed" );
        final ServletResponseBody body = ( request, response ) ->
        {
            throw failure;
        };
        final var resp = WebResponse.create().body( body ).build();
        final var httpResponse = mock( HttpServletResponse.class );

        assertThatThrownBy( () -> new ResponseSerializer( req, resp ).serialize( httpResponse ) ).isSameAs( failure );
    }

    @Test
    void serializeServletResponseBodyRequiresRawRequest()
    {
        final var body = mock( ServletResponseBody.class );
        final var resp = WebResponse.create().body( body ).build();
        final var httpResponse = mock( HttpServletResponse.class );

        assertThatThrownBy( () -> new ResponseSerializer( new WebRequest(), resp ).serialize( httpResponse ) ).
            isInstanceOf( IOException.class ).
            hasMessageContaining( "ServletResponseBody" ).
            hasMessageContaining( "set WebRequest.rawRequest before serialization" );
        verifyNoInteractions( body );
    }

    @Test
    void serializeCommittedResponseSkipsServletResponseBody()
        throws Exception
    {
        final var body = mock( ServletResponseBody.class );
        final var resp = WebResponse.create().body( body ).build();
        final var httpResponse = mock( HttpServletResponse.class );
        when( httpResponse.isCommitted() ).thenReturn( true );

        new ResponseSerializer( new WebRequest(), resp ).serialize( httpResponse );

        verify( httpResponse ).isCommitted();
        verifyNoMoreInteractions( httpResponse );
        verifyNoInteractions( body );
    }

    @ParameterizedTest
    @EnumSource(value = HttpStatus.class, names = {"NO_CONTENT", "NOT_MODIFIED"})
    void serializeStatusWithoutContentSkipsServletResponseBody( final HttpStatus status )
        throws Exception
    {
        final var body = mock( ServletResponseBody.class );
        final var resp = WebResponse.create().status( status ).body( body ).build();
        final var httpResponse = mock( HttpServletResponse.class );

        new ResponseSerializer( new WebRequest(), resp ).serialize( httpResponse );

        verify( httpResponse ).setStatus( status.value() );
        verify( httpResponse, never() ).setContentType( any() );
        verify( httpResponse, never() ).setContentLength( anyInt() );
        verify( httpResponse, never() ).setContentLengthLong( anyLong() );
        verify( httpResponse, never() ).getOutputStream();
        verifyNoInteractions( body );
    }

    @Test
    void serializeBodyString()
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
    void serializeBodyBytes()
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
    void serializeBodyByteSource()
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
    void serializeBodyResource()
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
    void serializeBodyJsonMap()
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
    void serializeHeadRequest()
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
    void serializeRequestCommitted()
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

    @Test
    void serializeContentSecurityPolicy()
        throws Exception
    {
        final WebRequest req = new WebRequest();
        req.setMethod( HttpMethod.GET );
        req.getContentSecurityPolicy().add( "script-src", "'self'" );

        final HttpServletResponse httpResponse = serializeEmptyResponse( req );

        verify( httpResponse ).setHeader( "Content-Security-Policy", "script-src 'self'" );
        verify( httpResponse, times( 0 ) ).setHeader( eq( "Content-Security-Policy-Report-Only" ), any() );
    }

    @Test
    void serializeNoContentSecurityPolicyWhenEmpty()
        throws Exception
    {
        final WebRequest req = new WebRequest();
        req.setMethod( HttpMethod.GET );

        final HttpServletResponse httpResponse = serializeEmptyResponse( req );

        verify( httpResponse, times( 0 ) ).setHeader( eq( "Content-Security-Policy" ), any() );
        verify( httpResponse, times( 0 ) ).setHeader( eq( "Content-Security-Policy-Report-Only" ), any() );
    }

    @Test
    void serializeContentSecurityPolicyOverridesDirectHeader()
        throws Exception
    {
        final WebRequest req = new WebRequest();
        req.setMethod( HttpMethod.GET );
        req.getContentSecurityPolicy().add( "script-src", "'self'" );

        final WebResponse resp = WebResponse.create().
            status( HttpStatus.OK ).
            contentType( MediaType.PLAIN_TEXT_UTF_8 ).
            header( "Content-Security-Policy", "default-src 'none'" ).
            build();

        final HttpServletResponse httpResponse = mock( HttpServletResponse.class );
        new ResponseSerializer( req, resp ).serialize( httpResponse );

        // the policy is written after the plain headers (WebResponse lowercases header names;
        // servlet setHeader is case-insensitive), so it wins on the servlet response
        final InOrder inOrder = inOrder( httpResponse );
        inOrder.verify( httpResponse ).setHeader( "content-security-policy", "default-src 'none'" );
        inOrder.verify( httpResponse ).setHeader( "Content-Security-Policy", "script-src 'self'" );
    }

    @Test
    void serializeContentSecurityPolicyKeepsDirectHeaderWhenPolicyEmpty()
        throws Exception
    {
        final WebRequest req = new WebRequest();
        req.setMethod( HttpMethod.GET );

        final WebResponse resp = WebResponse.create().
            status( HttpStatus.OK ).
            contentType( MediaType.PLAIN_TEXT_UTF_8 ).
            header( "Content-Security-Policy", "default-src 'none'" ).
            build();

        final HttpServletResponse httpResponse = mock( HttpServletResponse.class );
        new ResponseSerializer( req, resp ).serialize( httpResponse );

        verify( httpResponse ).setHeader( "content-security-policy", "default-src 'none'" );
        verify( httpResponse, times( 0 ) ).setHeader( eq( "Content-Security-Policy" ), any() );
    }

    @Test
    void serializeEnforcedAndReportOnlyPoliciesCoexist()
        throws Exception
    {
        final WebRequest req = new WebRequest();
        req.setMethod( HttpMethod.GET );
        req.getContentSecurityPolicy().add( "script-src", "'self'" );
        req.getContentSecurityPolicy().reportOnly().add( "script-src", "'none'" );

        final HttpServletResponse httpResponse = serializeEmptyResponse( req );

        verify( httpResponse ).setHeader( "Content-Security-Policy", "script-src 'self'" );
        verify( httpResponse ).setHeader( "Content-Security-Policy-Report-Only", "script-src 'none'" );
    }

    private static HttpServletResponse serializeEmptyResponse( final WebRequest req )
        throws Exception
    {
        final WebResponse resp = WebResponse.create().
            status( HttpStatus.OK ).
            contentType( MediaType.PLAIN_TEXT_UTF_8 ).
            build();
        final HttpServletResponse httpResponse = mock( HttpServletResponse.class );
        new ResponseSerializer( req, resp ).serialize( httpResponse );
        return httpResponse;
    }

    @ParameterizedTest
    @EnumSource(value = HttpStatus.class, names = {"NO_CONTENT", "NOT_MODIFIED"})
    void serializeStatusWithoutContent( final HttpStatus status )
        throws Exception
    {
        final WebRequest req = new WebRequest();
        req.setMethod( HttpMethod.GET );

        final WebResponse resp = WebResponse.create()
            .status( status )
            .contentType( MediaType.PNG )
            .header( "ETag", "\"abc\"" )
            .cookie( new Cookie( "cookie-name", "cookie-value" ) )
            .body( "String body" )
            .build();

        final HttpServletResponse httpResponse = mock( HttpServletResponse.class );

        new ResponseSerializer( req, resp ).serialize( httpResponse );

        verify( httpResponse ).setStatus( status.value() );
        verify( httpResponse ).setHeader( "etag", "\"abc\"" );
        verify( httpResponse ).addCookie( any() );
        verify( httpResponse, never() ).setContentType( any() );
        verify( httpResponse, never() ).setContentLengthLong( anyLong() );
        verify( httpResponse, never() ).getOutputStream();
    }
}
