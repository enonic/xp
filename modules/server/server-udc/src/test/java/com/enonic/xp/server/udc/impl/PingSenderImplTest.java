package com.enonic.xp.server.udc.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

public class PingSenderImplTest
{
    private PingSender sender;

    private MockWebServer server;

    @Before
    public void setup()
        throws Exception
    {
        this.server = new MockWebServer();
        this.server.start();

        final UdcInfoGenerator generator = new UdcInfoGenerator();
        final UdcUrlBuilder urlBuilder = new UdcUrlBuilder( "http://localhost:" + this.server.getPort() );
        this.sender = new PingSenderImpl( generator, urlBuilder );
    }

    @After
    public void shutdown()
        throws Exception
    {
        this.server.shutdown();
    }

    @Test
    public void testPing()
    {
        final MockResponse response = new MockResponse();
        response.setResponseCode( 200 );

        this.server.enqueue( response );
        this.sender.send();
    }
}
