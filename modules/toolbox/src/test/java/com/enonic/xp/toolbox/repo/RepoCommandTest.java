package com.enonic.xp.toolbox.repo;

import org.junit.After;
import org.junit.Before;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import com.enonic.xp.toolbox.ToolCommandTest;
import com.enonic.xp.toolbox.util.JsonHelper;

public abstract class RepoCommandTest
    extends ToolCommandTest
{
    protected MockWebServer server;

    @Before
    public final void setup()
        throws Exception
    {
        this.server = new MockWebServer();
        this.server.start();
    }

    @After
    public final void shutdown()
        throws Exception
    {
        this.server.shutdown();
    }

    protected final void configure( final RepoCommand command )
    {
        command.host = this.server.getHostName();
        command.port = this.server.getPort();
        command.auth = "user:password";
    }

    protected final MockResponse addResponse( final JsonNode json )
        throws Exception
    {
        return addResponse( JsonHelper.serialize( json ) ).
            addHeader( "Content-Type", "application/json; charset=utf-8" );
    }

    protected final MockResponse addResponse( final String json )
        throws Exception
    {
        final MockResponse response = new MockResponse();
        response.setBody( json );
        this.server.enqueue( response );
        return response;
    }

    protected final RecordedRequest takeRequest()
        throws Exception
    {
        return this.server.takeRequest();
    }
}
