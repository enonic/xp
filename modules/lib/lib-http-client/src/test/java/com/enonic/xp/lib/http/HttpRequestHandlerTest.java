package com.enonic.xp.lib.http;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;

import com.enonic.xp.testing.script.ScriptTestSupport;

import static org.junit.Assert.*;

public class HttpRequestHandlerTest
    extends ScriptTestSupport
{
    protected MockWebServer server;

    @Override
    public void initialize()
        throws Exception
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
        response.setHeader( "content-type", "text/plain" );
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

        runFunction( "/site/test/request-test.js", "simpleGetRequest", getServerHost() );

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

        runFunction( "/site/test/request-test.js", "simplePostRequest", getServerHost() );

        final RecordedRequest request = takeRequest();
        assertEquals( "POST", request.getMethod() );
        assertEquals( "/my/url", request.getPath() );
        assertEquals( "POST body", request.getBody().readString( Charsets.UTF_8 ) );
    }

    @Test
    public void testSimpleHeadRequest()
        throws Exception
    {
        server.enqueue( addResponse( "GET request" ) );

        runFunction( "/site/test/request-test.js", "simpleHeadRequest", getServerHost() );

        final RecordedRequest request = takeRequest();
        assertEquals( "HEAD", request.getMethod() );
        assertEquals( "/my/url", request.getPath() );
        assertEquals( "", request.getBody().readString( Charsets.UTF_8 ) );
    }

    @Test
    public void testGetRequestWithParams()
        throws Exception
    {
        addResponse( "GET request" );

        runFunction( "/site/test/request-test.js", "getRequestWithParams", getServerHost() );

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

        runFunction( "/site/test/request-test.js", "postRequestWithParams", getServerHost() );

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

        runFunction( "/site/test/request-test.js", "postJsonRequest", getServerHost() );

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

        runFunction( "/site/test/request-test.js", "getWithHeadersRequest", getServerHost() );

        final RecordedRequest request = takeRequest();
        assertEquals( "GET", request.getMethod() );
        assertEquals( "some-value", request.getHeader( "X-Custom-Header" ) );
    }

    @Test
    public void testReadTimeout()
        throws Exception
    {
        addResponseWithDelay( "GET request", 2000 );

        runFunction( "/site/test/request-test.js", "getWithResponseTimeout", getServerHost() );

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

        runFunction( "/site/test/request-test.js", "getWithConnectTimeout", getServerHost() );

        final RecordedRequest request = takeRequest();
        assertEquals( "GET", request.getMethod() );
    }

    @Test
    public void testRequestWithProxy()
        throws Exception
    {
        final MockWebServer proxy = new MockWebServer();
        try
        {
            proxy.start();

            final MockResponse response = new MockResponse();
            response.setBody( "POST request" );
            response.setHeader( "content-type", "text/plain" );
            proxy.enqueue( response );

            runFunction( "/site/test/request-test.js", "requestWithProxy", getServerHost(), proxy.getHostName(), proxy.getPort() );

            final RecordedRequest proxyRequest = proxy.takeRequest();
            assertEquals( "POST", proxyRequest.getMethod() );
            assertEquals( "http://" + server.getHostName() + ":" + server.getPort() + "/my/url", proxyRequest.getPath() );
        }
        finally
        {
            proxy.shutdown();
        }
    }

    @Test
    public void testRequestWithProxyWithAuth()
        throws Exception
    {
        final MockWebServer proxy = new MockWebServer();
        try
        {
            proxy.start();

            final MockResponse proxyAuthResponse = new MockResponse();
            proxyAuthResponse.setResponseCode( 407 );
            proxy.enqueue( proxyAuthResponse );

            final MockResponse response = new MockResponse();
            response.setBody( "POST request authenticated" );
            response.setHeader( "content-type", "text/plain" );
            proxy.enqueue( response );

            runFunction( "/site/test/request-test.js", "requestWithProxyAuth", getServerHost(), proxy.getHostName(), proxy.getPort() );

            final RecordedRequest proxyRequest = proxy.takeRequest();
            assertEquals( "POST", proxyRequest.getMethod() );
            assertEquals( "http://" + server.getHostName() + ":" + server.getPort() + "/my/url", proxyRequest.getPath() );
        }
        finally
        {
            proxy.shutdown();
        }
    }

    public String getServerHost()
    {
        return server.getHostName() + ":" + server.getPort();
    }

    public ByteSource getImageStream()
    {
        return ByteSource.wrap( "image_data".getBytes() );
    }

    @Test
    public void testExample()
        throws Exception
    {
        this.server.enqueue( addResponse( "POST request" ) );
        runScript( "/site/lib/xp/examples/http-client/request.js" );
    }

    @Test
    public void testExampleMultipart()
        throws Exception
    {
        this.server.enqueue( addResponse( "POST request" ) );
        runScript( "/site/lib/xp/examples/http-client/multipart.js" );
    }

    @Test
    public void testBasicAuthentication()
        throws Exception
    {
        final MockResponse response = addResponse( "POST request" );
        response.setResponseCode( 401 );
        response.setHeader( "WWW-Authenticate", "Basic realm=\"foo\", charset=\"UTF-8\"" );
        this.server.enqueue( response );
        runScript( "/site/lib/xp/examples/http-client/basicauth.js" );

        final RecordedRequest request = takeRequest();
        assertEquals( "GET", request.getMethod() );
        assertEquals( "Basic dXNlcm5hbWU6c2VjcmV0", request.getHeader( "Authorization" ) );
    }
}
