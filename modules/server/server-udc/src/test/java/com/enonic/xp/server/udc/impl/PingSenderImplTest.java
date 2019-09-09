package com.enonic.xp.server.udc.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import static org.junit.jupiter.api.Assertions.*;

public class PingSenderImplTest
{
    private PingSender sender;

    private MockWebServer server;

    @BeforeEach
    public void setup()
        throws Exception
    {
        this.server = new MockWebServer();
        this.server.start();

        final UdcInfoGenerator generator = new UdcInfoGenerator();
        this.sender = new PingSenderImpl( generator, "http://localhost:" + this.server.getPort() );
    }

    @AfterEach
    public void shutdown()
        throws Exception
    {
        this.server.shutdown();
    }

    @Test
    public void testPing()
        throws Exception
    {
        final MockResponse response = new MockResponse();
        response.setResponseCode( 200 );

        this.server.enqueue( response );
        this.sender.send();

        final RecordedRequest req = this.server.takeRequest();
        assertEquals( "POST", req.getMethod() );
    }
}
