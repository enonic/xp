package com.enonic.xp.portal.impl;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Joiner;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
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
        addHandler( dispatcher );
    }

    @Test
    @Ignore("need exception mapper")
    public void testNoBranch()
        throws Exception
    {
        final Request request = newRequest( "/portal2/" ).
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

        final Request request = newRequest( "/portal2/master/a/b" ).
            get().
            build();

        final Response response = callRequest( request );

        assertEquals( 200, response.code() );
        assertEquals( "text/plain", response.body().contentType().toString() );
        assertEquals( "Hello World", response.body().string() );
        assertEquals( 11, response.body().contentLength() );

        assertNotNull( this.handler.request );
        assertEquals( "master", this.handler.request.getBranch().toString() );
        assertEquals( RenderMode.LIVE, this.handler.request.getMode() );
        assertEquals( "" + this.server.getPort(), this.handler.request.getPort() );
        assertEquals( "localhost", this.handler.request.getHost() );
        assertEquals( "/portal2/master/a/b", this.handler.request.getPath() );
        assertEquals( "http", this.handler.request.getScheme() );
        assertEquals( "http://localhost:" + this.server.getPort() + "/portal2/master/a/b", this.handler.request.getUrl() );
        assertEquals( "GET", this.handler.request.getMethod() );
        assertEquals( "/a/b", this.handler.request.getContentPath().toString() );
        assertEquals( "/portal2", this.handler.request.getBaseUri() );
    }

    @Test
    public void testResponseHeaders()
        throws Exception
    {
        this.handler.response = PortalResponse.create().
            status( 200 ).
            header( "X-Header", "Value" ).
            build();

        final Request request = newRequest( "/portal2/master/a/b" ).
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
        this.handler.response = PortalResponse.create().
            status( 200 ).
            build();

        final Request request = newRequest( "/portal2/master/a/b" ).
            get().
            header( "X-Header", "Value" ).
            build();

        final Response response = callRequest( request );

        assertEquals( 200, response.code() );
        assertEquals( "Value", this.handler.request.getHeaders().get( "X-Header" ) );
    }

    @Test
    public void testReadCookies()
        throws Exception
    {
        this.handler.response = PortalResponse.create().
            status( 200 ).
            build();

        final Request request = newRequest( "/portal2/master/a/b" ).
            get().
            header( "Cookie", "theme=light; sessionToken=abc123" ).
            build();

        final Response response = callRequest( request );

        assertEquals( 200, response.code() );
        assertEquals( "light", this.handler.request.getCookies().get( "theme" ) );
        assertEquals( "abc123", this.handler.request.getCookies().get( "sessionToken" ) );
    }

    @Test
    public void testParameters()
        throws Exception
    {
        this.handler.response = PortalResponse.create().
            status( 200 ).
            build();

        final Request request = newRequest( "/portal2/master/a/b?a=1&b=2&b=3" ).
            get().
            build();

        final Response response = callRequest( request );

        assertEquals( 200, response.code() );
        assertEquals( "1", Joiner.on( "," ).join( this.handler.request.getParams().get( "a" ) ) );
        assertEquals( "2,3", Joiner.on( "," ).join( this.handler.request.getParams().get( "b" ) ) );
    }
}
