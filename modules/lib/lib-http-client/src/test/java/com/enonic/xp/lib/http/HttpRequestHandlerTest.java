package com.enonic.xp.lib.http;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import com.squareup.okhttp.mockwebserver.SocketPolicy;

import com.enonic.xp.testing.script.ScriptTestSupport;

import static org.junit.Assert.*;

public class HttpRequestHandlerTest
    extends ScriptTestSupport
{
    protected MockWebServer server;

    @Override
    public void initialize()
    {
        super.initialize();
        this.server = new MockWebServer();
        try
        {
            this.server.start();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    @After
    public final void shutdown()
        throws Exception
    {
        this.server.shutdown();
    }

    private MockResponse addResponse( final String body )
        throws Exception
    {
        final MockResponse response = new MockResponse();
        response.setBody( body );
        this.server.enqueue( response );
        return response;
    }

    private MockResponse addResponseWithDelay( final String body, final long millis )
        throws Exception
    {
        final MockResponse response = new MockResponse();
        response.setBody( body );
        response.throttleBody( 0, millis, TimeUnit.MILLISECONDS );
        this.server.enqueue( response );
        return response;
    }

    private RecordedRequest takeRequest()
        throws Exception
    {
        return this.server.takeRequest();
    }

    @Test
    public void testSimpleGetRequest()
        throws Exception
    {
        addResponse( "GET request" );

        runFunction( "/site/test/request-test.js", "simpleGetRequest", serverHost() );

        final RecordedRequest request = takeRequest();
        assertEquals( "GET", request.getMethod() );
        assertEquals( "/my/url", request.getPath() );
        assertEquals( "", request.getBody().readString( Charsets.UTF_8 ) );
    }

    @Test
    public void testSimplePostRequest()
        throws Exception
    {
        server.enqueue( addResponse( "POST request" ) );

        runFunction( "/site/test/request-test.js", "simplePostRequest", serverHost() );

        final RecordedRequest request = takeRequest();
        assertEquals( "POST", request.getMethod() );
        assertEquals( "/my/url", request.getPath() );
        assertEquals( "POST body", request.getBody().readString( Charsets.UTF_8 ) );
    }

    @Test
    public void testGetRequestWithParams()
        throws Exception
    {
        addResponse( "GET request" );

        runFunction( "/site/test/request-test.js", "getRequestWithParams", serverHost() );

        final RecordedRequest request = takeRequest();
        assertEquals( "GET", request.getMethod() );
        assertEquals( "/my/url?a=123&b=456", request.getPath() );
        assertEquals( "", request.getBody().readString( Charsets.UTF_8 ) );
    }

    @Test
    public void testPostRequestWithParams()
        throws Exception
    {
        addResponse( "POST request" );

        runFunction( "/site/test/request-test.js", "postRequestWithParams", serverHost() );

        final RecordedRequest request = takeRequest();
        assertEquals( "POST", request.getMethod() );
        assertEquals( "/my/url", request.getPath() );
        assertEquals( "a=123&b=456", request.getBody().readString( Charsets.UTF_8 ) );
    }

    @Test
    public void testPostJsonRequest()
        throws Exception
    {
        addResponse( "POST request" );

        runFunction( "/site/test/request-test.js", "postJsonRequest", serverHost() );

        final RecordedRequest request = takeRequest();
        assertEquals( "POST", request.getMethod() );
        assertEquals( "/my/url", request.getPath() );
        assertEquals( "application/json; charset=utf-8", request.getHeader( "content-type" ) );
        assertEquals( "{\"a\":123,\"b\":456}", request.getBody().readString( Charsets.UTF_8 ) );
    }

    @Test
    public void testGetWithHeadersRequest()
        throws Exception
    {
        addResponse( "GET request" );

        runFunction( "/site/test/request-test.js", "getWithHeadersRequest", serverHost() );

        final RecordedRequest request = takeRequest();
        assertEquals( "GET", request.getMethod() );
        assertEquals( "some-value", request.getHeader( "X-Custom-Header" ) );
    }

    @Test
    public void testReadTimeout()
        throws Exception
    {
        addResponseWithDelay( "GET request", 2000 );

        runFunction( "/site/test/request-test.js", "getWithResponseTimeout", serverHost() );

        final RecordedRequest request = takeRequest();
        assertEquals( "GET", request.getMethod() );
    }

    @Test
    public void testConnectTimeout()
        throws Exception
    {
        MockResponse response = new MockResponse();
        response = response.setSocketPolicy( SocketPolicy.NO_RESPONSE );
        server.enqueue( response );

        runFunction( "/site/test/request-test.js", "getWithConnectTimeout", serverHost() );

        final RecordedRequest request = takeRequest();
        assertEquals( "GET", request.getMethod() );
    }

    private String serverHost()
    {
        return server.getHostName() + ":" + server.getPort();
    }
}
