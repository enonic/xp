package com.enonic.xp.portal.impl;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import com.google.common.base.Joiner;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import com.enonic.xp.portal.PortalAttributes;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.exception.ExceptionRendererImpl;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.filter.BaseWebFilter;
import com.enonic.xp.web.jetty.impl.JettyTestSupport;

import static org.junit.Assert.*;

public class PortalDispatcherTest
    extends JettyTestSupport
{
    private PortalDispatcher servlet;

    private TestPortalHandler handler;

    @Override
    protected void configure()
        throws Exception
    {
        this.handler = new TestPortalHandler();

        this.servlet = new PortalDispatcher();
        this.servlet.addHandler( this.handler );
        this.servlet.setExceptionRenderer( new ExceptionRendererImpl() );

        this.handler.response = PortalResponse.create().
            status( HttpStatus.OK ).
            build();

        addServlet( this.servlet, "/portal/*" );
    }

    @Test
    public void testNoBranch()
        throws Exception
    {
        final Request request = newRequest( "/portal/" ).
            get().
            build();

        final Response response = callRequest( request );
        assertEquals( 404, response.code() );
    }


    @Test
    public void testSimpleGet()
        throws Exception
    {
        this.handler.response = PortalResponse.create().
            status( HttpStatus.OK ).
            contentType( com.google.common.net.MediaType.create( "text", "plain" ) ).
            body( "Hello World" ).
            build();

        final Request request = newRequest( "/portal/master/a/b" ).
            get().
            build();

        this.handler.verifier = req -> {
            assertEquals( "master", req.getBranch().toString() );
            assertEquals( RenderMode.LIVE, req.getMode() );
            assertEquals( this.server.getPort(), req.getPort() );
            assertEquals( "localhost", req.getHost() );
            assertEquals( "/portal/master/a/b", req.getPath() );
            assertEquals( "http", req.getScheme() );
            assertEquals( "http://localhost:" + this.server.getPort() + "/portal/master/a/b", req.getUrl() );
            assertEquals( HttpMethod.GET, req.getMethod() );
            assertEquals( "/a/b", req.getContentPath().toString() );
            assertEquals( "/portal", req.getBaseUri() );
        };

        final Response response = callRequest( request );

        assertEquals( 200, response.code() );
        assertEquals( "text/plain", response.body().contentType().toString() );
        assertEquals( "Hello World", response.body().string() );
        assertEquals( 11, response.body().contentLength() );
    }

    @Test
    public void testResponseHeaders()
        throws Exception
    {
        this.handler.response = PortalResponse.create().
            status( HttpStatus.OK ).
            header( "X-Header", "Value" ).
            build();

        final Request request = newRequest( "/portal/master/a/b" ).
            get().
            build();

        final Response response = callRequest( request );
        assertEquals( 200, response.code() );
        assertEquals( "Value", response.header( "X-Header" ) );
    }

    @Test
    public void testRequestHeaders()
        throws Exception
    {
        final Request request = newRequest( "/portal/master/a/b" ).
            get().
            header( "X-Header", "Value" ).
            build();

        this.handler.verifier = req -> {
            assertEquals( 5, req.getHeaders().size() );
            assertEquals( "Value", req.getHeaders().get( "X-Header" ) );
        };

        final Response response = callRequest( request );
        assertEquals( 200, response.code() );
    }

    @Test
    public void testReadCookies()
        throws Exception
    {
        final Request request = newRequest( "/portal/master/a/b" ).
            get().
            header( "Cookie", "theme=light; sessionToken=abc123" ).
            build();

        this.handler.verifier = req -> {
            assertEquals( "light", req.getCookies().get( "theme" ) );
            assertEquals( "abc123", req.getCookies().get( "sessionToken" ) );
        };

        final Response response = callRequest( request );
        assertEquals( 200, response.code() );
    }

    @Test
    public void testParameters()
        throws Exception
    {
        final Request request = newRequest( "/portal/master/a/b?a=1&b=2&b=3" ).
            get().
            build();

        this.handler.verifier = req -> {
            assertEquals( "1", Joiner.on( "," ).join( req.getParams().get( "a" ) ) );
            assertEquals( "2,3", Joiner.on( "," ).join( req.getParams().get( "b" ) ) );
        };

        final Response response = callRequest( request );
        assertEquals( 200, response.code() );
    }

    @Test
    public void testPost_formEncoded()
        throws Exception
    {
        final RequestBody formBody = new FormEncodingBuilder().
            add( "search", "Jurassic Park" ).
            add( "expand", "true" ).
            build();

        final Request request = newRequest( "/portal/master/a/b" ).
            post( formBody ).
            build();

        this.handler.verifier = req -> {
            assertEquals( HttpMethod.POST, req.getMethod() );
            assertEquals( "application/x-www-form-urlencoded", req.getContentType() );
            assertEquals( "Jurassic Park", Joiner.on( "," ).join( req.getParams().get( "search" ) ) );
            assertEquals( "true", Joiner.on( "," ).join( req.getParams().get( "expand" ) ) );
        };

        final Response response = callRequest( request );
        assertEquals( 200, response.code() );
    }

    @Test
    public void testPost_plainText()
        throws Exception
    {
        final RequestBody formBody = RequestBody.create( MediaType.parse( "text/plain" ), "Hello World" );

        final Request request = newRequest( "/portal/master/a/b" ).
            post( formBody ).
            build();

        this.handler.verifier = req -> {
            assertEquals( HttpMethod.POST, req.getMethod() );
            assertEquals( "text/plain; charset=UTF-8", req.getContentType() );
            assertEquals( "Hello World", req.getBodyAsString() );
        };

        final Response response = callRequest( request );
        assertEquals( 200, response.code() );
    }

    @Test
    public void testPost_json()
        throws Exception
    {
        final RequestBody formBody = RequestBody.create( MediaType.parse( "application/json" ), "{}" );

        final Request request = newRequest( "/portal/master/a/b" ).
            post( formBody ).
            build();

        this.handler.verifier = req -> {
            assertEquals( HttpMethod.POST, req.getMethod() );
            assertEquals( "application/json; charset=utf-8", req.getContentType() );
            assertEquals( "{}", req.getBodyAsString() );
        };

        final Response response = callRequest( request );
        assertEquals( 200, response.code() );
    }

    @Test
    public void testRemoveHandler()
        throws Exception
    {
        this.servlet.removeHandler( this.handler );

        final Request request = newRequest( "/portal/master/a/b" ).
            get().
            build();

        final Response response = callRequest( request );
        assertEquals( 404, response.code() );
    }

    @Test
    public void testPortalAttributes()
        throws Exception
    {
        final Request request = newRequest( "/portal/master/a/b" ).
            get().
            build();

        addFilter( new BaseWebFilter()
        {
            @Override
            protected void doFilter( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
                throws Exception
            {
                final PortalAttributes attributes = new PortalAttributes();
                attributes.setBaseUri( "/other" );
                attributes.setRenderMode( RenderMode.EDIT );

                req.setAttribute( PortalAttributes.class.getName(), attributes );
                chain.doFilter( req, res );
            }
        }, "/*" );

        this.handler.verifier = req -> {
            assertEquals( "/other", req.getBaseUri() );
            assertEquals( RenderMode.EDIT, req.getMode() );
        };

        final Response response = callRequest( request );
        assertEquals( 200, response.code() );
    }
}
