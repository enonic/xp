package com.enonic.xp.server.udc.impl;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PingSenderTest
{
    private PingSender sender;

    private HttpServer server;

    @BeforeEach
    void setup()
        throws Exception
    {
        this.server = HttpServer.create( new InetSocketAddress( 0 ), 0 );
        this.server.start();

        final UdcInfoGenerator generator = new UdcInfoGenerator();
        this.sender = new PingSender( generator, "http://localhost:" + this.server.getAddress().getPort() );
    }

    @AfterEach
    void shutdown()
    {
        this.server.stop( 0 );
    }

    @Test
    void testPing()
        throws Exception
    {
        CompletableFuture<String> requestMethod = new CompletableFuture<>();
        this.server.createContext( "/", exchange -> {
            requestMethod.complete( exchange.getRequestMethod() );
            exchange.sendResponseHeaders( 200, 0 );
            exchange.close();
        } );
        this.sender.run();

        assertEquals( "POST", requestMethod.get( 1, TimeUnit.MINUTES ) );
    }
}
