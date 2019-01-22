package com.enonic.xp.web.impl.handler;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Joiner;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import com.enonic.xp.portal.impl.exception.ExceptionRendererImpl;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.impl.exception.ExceptionMapperImpl;
import com.enonic.xp.web.impl.serializer.ResponseSerializationServiceImpl;
import com.enonic.xp.web.jetty.impl.JettyTestSupport;
import com.enonic.xp.web.websocket.WebSocketContextFactory;

import static org.junit.Assert.*;

public class WebDispatcherServletTest
    extends JettyTestSupport
{
    private WebDispatcherServlet servlet;

    private TestWebHandler handler;

    @Override
    protected void configure()
        throws Exception
    {
        this.handler = new TestWebHandler();

        this.servlet = new WebDispatcherServlet();
        this.servlet.setWebDispatcher( new WebDispatcherImpl() );
        this.servlet.addWebHandler( this.handler );
        this.servlet.setExceptionMapper( new ExceptionMapperImpl() );
        this.servlet.setExceptionRenderer( new ExceptionRendererImpl() );
        this.servlet.setWebSocketContextFactory( Mockito.mock( WebSocketContextFactory.class ) );
        this.servlet.setResponseSerializationService( new ResponseSerializationServiceImpl() );

        this.handler.response = WebResponse.create().
            status( HttpStatus.OK ).
            build();

        addServlet( this.servlet, "/site/*" );
    }

    @Test
    public void testSimpleGet()
        throws Exception
    {
        this.handler.response = WebResponse.create().
            status( HttpStatus.OK ).
            contentType( com.google.common.net.MediaType.create( "text", "plain" ) ).
            body( "Hello World" ).
            build();

        final Request request = newRequest( "/site/master/a/b" ).
            get().
            build();

        this.handler.verifier = req -> {
            Assert.assertEquals( this.server.getPort(), req.getPort() );
            Assert.assertEquals( "localhost", req.getHost() );
            Assert.assertEquals( "/site/master/a/b", req.getPath() );
            Assert.assertEquals( "http", req.getScheme() );
            Assert.assertEquals( "http://localhost:" + this.server.getPort() + "/site/master/a/b", req.getUrl() );
            Assert.assertEquals( HttpMethod.GET, req.getMethod() );
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
        this.handler.response = WebResponse.create().
            status( HttpStatus.OK ).
            header( "X-Header", "Value" ).
            build();

        final Request request = newRequest( "/site/master/a/b" ).
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
        final Request request = newRequest( "/site/master/a/b" ).
            get().
            header( "X-Header", "Value" ).
            build();

        this.handler.verifier = req -> {
            Assert.assertEquals( 5, req.getHeaders().size() );
            Assert.assertEquals( "Value", req.getHeaders().get( "X-Header" ) );
        };

        final Response response = callRequest( request );
        assertEquals( 200, response.code() );
    }

    @Test
    public void testReadCookies()
        throws Exception
    {
        final Request request = newRequest( "/site/master/a/b" ).
            get().
            header( "Cookie", "theme=light; sessionToken=abc123" ).
            build();

        this.handler.verifier = req -> {
            Assert.assertEquals( "light", req.getCookies().get( "theme" ) );
            Assert.assertEquals( "abc123", req.getCookies().get( "sessionToken" ) );
        };

        final Response response = callRequest( request );
        assertEquals( 200, response.code() );
    }

    @Test
    public void testParameters()
        throws Exception
    {
        final Request request = newRequest( "/site/master/a/b?a=1&b=2&b=3" ).
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

        final Request request = newRequest( "/site/master/a/b" ).
            post( formBody ).
            build();

        this.handler.verifier = req -> {
            Assert.assertEquals( HttpMethod.POST, req.getMethod() );
            Assert.assertEquals( "application/x-www-form-urlencoded", req.getContentType() );
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

        final Request request = newRequest( "/site/master/a/b" ).
            post( formBody ).
            build();

        this.handler.verifier = req -> {
            Assert.assertEquals( HttpMethod.POST, req.getMethod() );
            Assert.assertEquals( "text/plain; charset=UTF-8", req.getContentType() );
            Assert.assertEquals( "Hello World", req.getBodyAsString() );
        };

        final Response response = callRequest( request );
        assertEquals( 200, response.code() );
    }

    @Test
    public void testPost_json()
        throws Exception
    {
        final RequestBody formBody = RequestBody.create( MediaType.parse( "application/json" ), "{}" );

        final Request request = newRequest( "/site/master/a/b" ).
            post( formBody ).
            build();

        this.handler.verifier = req -> {
            Assert.assertEquals( HttpMethod.POST, req.getMethod() );
            Assert.assertEquals( "application/json; charset=utf-8", req.getContentType().toLowerCase() );
            Assert.assertEquals( "{}", req.getBodyAsString() );
        };

        final Response response = callRequest( request );
        assertEquals( 200, response.code() );
    }

    @Test
    public void testRemoveHandler()
        throws Exception
    {
        this.servlet.removeWebHandler( this.handler );

        final Request request = newRequest( "/site/master/a/b" ).
            get().
            build();

        final Response response = callRequest( request );
        assertEquals( 404, response.code() );
    }
}
