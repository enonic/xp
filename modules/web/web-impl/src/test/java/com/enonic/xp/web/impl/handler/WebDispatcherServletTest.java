package com.enonic.xp.web.impl.handler;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.impl.exception.ExceptionMapperImpl;
import com.enonic.xp.web.impl.serializer.ResponseSerializationServiceImpl;
import com.enonic.xp.web.jetty.impl.JettyTestSupport;
import com.enonic.xp.web.websocket.WebSocketContextFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WebDispatcherServletTest
    extends JettyTestSupport
{
    private WebDispatcherServlet servlet;

    private TestWebHandler handler;

    @Override
    protected void configure()
    {
        this.handler = new TestWebHandler();

        this.servlet = new WebDispatcherServlet( new WebDispatcherImpl() );
        this.servlet.addWebHandler( this.handler );
        this.servlet.setExceptionMapper( new ExceptionMapperImpl() );
        this.servlet.setExceptionRenderer( ( req, cause ) -> WebResponse.create().status( HttpStatus.NOT_FOUND ).build() );
        this.servlet.setWebSocketContextFactory( Mockito.mock( WebSocketContextFactory.class ) );
        this.servlet.setResponseSerializationService( new ResponseSerializationServiceImpl() );

        this.handler.response = WebResponse.create().
            status( HttpStatus.OK ).
            build();

        addServlet( this.servlet, "/site/*" );
    }

    @Test
    void testSimpleGet()
        throws Exception
    {
        this.handler.response = WebResponse.create().
            status( HttpStatus.OK ).
            contentType( com.google.common.net.MediaType.create( "text", "plain" ) ).
            body( "Hello World" ).
            build();

        final HttpRequest request = newRequest( "/site/master/a/b" ).
            GET().
            build();

        this.handler.verifier = req -> {
            assertEquals( this.server.getPort(), req.getPort() );
            assertEquals( "localhost", req.getHost() );
            assertEquals( "/site/master/a/b", req.getPath() );
            assertEquals( "http", req.getScheme() );
            assertEquals( "http://localhost:" + this.server.getPort() + "/site/master/a/b", req.getUrl() );
            assertEquals( HttpMethod.GET, req.getMethod() );
        };

        final HttpResponse response = callRequest( request );

        assertEquals( 200, response.statusCode() );
        assertEquals( List.of( "text/plain" ), response.headers().allValues( "content-type" ) );
        assertEquals( "Hello World", response.body().toString() );
        assertEquals( List.of( "11" ), response.headers().allValues( "content-length" ) );
    }

    @Test
    void testResponseHeaders()
        throws Exception
    {
        this.handler.response = WebResponse.create().
            status( HttpStatus.OK ).
            header( "X-Header", "Value" ).
            build();

        final HttpRequest request = newRequest( "/site/master/a/b" ).
            GET().
            build();

        final HttpResponse response = callRequest( request );
        assertEquals( 200, response.statusCode() );
        assertEquals( List.of( "Value" ), response.headers().allValues( "X-Header" ) );
    }

    @Test
    void testRequestHeaders()
        throws Exception
    {
        final HttpRequest request = newRequest( "/site/master/a/b" ).
            GET().
            header( "X-Header", "Value" ).
            build();

        this.handler.verifier = req -> {
            assertEquals( "Value", req.getHeaders().get( "X-Header" ) );
        };

        final HttpResponse response = callRequest( request );
        assertEquals( 200, response.statusCode() );
    }

    @Test
    void testReadCookies()
        throws Exception
    {
        final HttpRequest request = newRequest( "/site/master/a/b" ).
            GET().
            header( "Cookie", "theme=light; sessionToken=abc123" ).
            build();

        this.handler.verifier = req -> {
            assertEquals( "light", req.getCookies().get( "theme" ) );
            assertEquals( "abc123", req.getCookies().get( "sessionToken" ) );
        };

        final HttpResponse response = callRequest( request );
        assertEquals( 200, response.statusCode() );
    }

    @Test
    void testParameters()
        throws Exception
    {
        final HttpRequest request = newRequest( "/site/master/a/b?a=1&b=2&b=3&b=3" ).
            GET().
            build();

        this.handler.verifier = req -> {
            assertAll( () -> assertThat( req.getParams().get( "a" ) ).containsExactly( "1" ),
                       () -> assertThat( req.getParams().get( "b" ) ).containsExactly( "2", "3", "3" ) );
        };

        final HttpResponse response = callRequest( request );
        assertEquals( 200, response.statusCode() );
    }

    @Test
    void testPost_formEncoded()
        throws Exception
    {
        HttpRequest.BodyPublisher formBody = HttpRequest.BodyPublishers.ofString( Map.of( "search", "Jurassic Park", "expand", "true" ).
            entrySet().
            stream().
            map( e -> e.getKey() + "=" + e.getValue() ).collect( Collectors.joining( "&" ) ) );
        final HttpRequest request = newRequest( "/site/master/a/b" ).
            header( "Content-Type", "application/x-www-form-urlencoded" ).
            POST( formBody ).
            build();

        this.handler.verifier = req -> {
            assertEquals( HttpMethod.POST, req.getMethod() );
            assertEquals( "application/x-www-form-urlencoded", req.getContentType() );
            assertEquals( "Jurassic Park", String.join( ",", req.getParams().get( "search" ) ) );
            assertEquals( "true", String.join( ",", req.getParams().get( "expand" ) ) );
        };

        final HttpResponse response = callRequest( request );
        assertEquals( 200, response.statusCode() );
    }

    @Test
    void testPost_plainText()
        throws Exception
    {
        HttpRequest.BodyPublisher formBody = HttpRequest.BodyPublishers.ofString( "Hello World" );

        final HttpRequest request = newRequest( "/site/master/a/b" ).
            header( "content-type", "text/plain; charset=utf-8" ).
            POST( formBody ).
            build();

        this.handler.verifier = req -> {
            assertEquals( HttpMethod.POST, req.getMethod() );
            assertEquals( "text/plain; charset=UTF-8", req.getContentType() );
            assertEquals( "Hello World", req.getBodyAsString() );
        };

        final HttpResponse response = callRequest( request );
        assertEquals( 200, response.statusCode() );
    }

    @Test
    void testPost_json()
        throws Exception
    {
        HttpRequest.BodyPublisher formBody = HttpRequest.BodyPublishers.ofString( "{}" );

        final HttpRequest request = newRequest( "/site/master/a/b" ).
            header( "content-type", "application/json; charset=utf-8" ).
            POST( formBody ).
            build();

        this.handler.verifier = req -> {
            assertEquals( HttpMethod.POST, req.getMethod() );
            assertEquals( "application/json; charset=utf-8", req.getContentType().toLowerCase() );
            assertEquals( "{}", req.getBodyAsString() );
        };

        final HttpResponse response = callRequest( request );
        assertEquals( 200, response.statusCode() );
    }

    @Test
    void testRemoveHandler()
        throws Exception
    {
        this.servlet.removeWebHandler( this.handler );

        final HttpRequest request = newRequest( "/site/master/a/b" ).
            GET().
            build();

        final HttpResponse response = callRequest( request );
        assertEquals( 404, response.statusCode() );
    }
}
