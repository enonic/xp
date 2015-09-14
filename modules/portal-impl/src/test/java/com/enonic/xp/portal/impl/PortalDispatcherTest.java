package com.enonic.xp.portal.impl;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Joiner;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.exception.ExceptionRendererImpl;
import com.enonic.xp.web.impl.WebHandlerTestSupport;

import static org.junit.Assert.*;

public class PortalDispatcherTest
    extends WebHandlerTestSupport
{
    private TestPortalHandler handler;

    @Before
    public void setup()
    {
        this.handler = new TestPortalHandler();

        final PortalDispatcher dispatcher = new PortalDispatcher();
        dispatcher.addHandler( this.handler );
        dispatcher.setExceptionRenderer( new ExceptionRendererImpl() );
        addHandler( dispatcher );

        this.handler.response = PortalResponse.create().
            status( 200 ).
            build();
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
            status( 200 ).
            contentType( "text/plain" ).
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
            assertEquals( "GET", req.getMethod() );
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
    public void testPortalAttributes()
        throws Exception
    {
        final Request request = newRequest( "/admin/portal/preview/master/a/b" ).
            get().
            build();

        this.handler.verifier = req -> {
            assertEquals( "/admin/portal/preview", req.getBaseUri() );
            assertEquals( RenderMode.PREVIEW, req.getMode() );
        };

        final Response response = callRequest( request );
        assertEquals( 200, response.code() );
    }

    @Test
    public void testResponseHeaders()
        throws Exception
    {
        this.handler.response = PortalResponse.create().
            status( 200 ).
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
            assertEquals( "POST", req.getMethod() );
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
            assertEquals( "POST", req.getMethod() );
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
            assertEquals( "POST", req.getMethod() );
            assertEquals( "application/json; charset=utf-8", req.getContentType() );
            assertEquals( "{}", req.getBodyAsString() );
        };

        final Response response = callRequest( request );
        assertEquals( 200, response.code() );
    }
}
