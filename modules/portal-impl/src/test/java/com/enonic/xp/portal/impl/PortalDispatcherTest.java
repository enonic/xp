package com.enonic.xp.portal.impl;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

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
}
