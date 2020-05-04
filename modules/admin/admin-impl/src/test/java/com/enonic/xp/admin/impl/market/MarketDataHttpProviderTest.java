package com.enonic.xp.admin.impl.market;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

import com.enonic.xp.market.MarketException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MarketDataHttpProviderTest
{
    private MarketDataHttpProvider provider;

    private HttpServer server;

    private String marketUrl;

    @BeforeEach
    void setUp()
        throws Exception
    {
        server = HttpServer.create( new InetSocketAddress( 0 ), 0 );
        server.start();
        marketUrl = "http://localhost:" + this.server.getAddress().getPort();

        provider = new MarketDataHttpProvider( new MarketConfig()
        {
            @Override
            public String marketUrl()
            {
                return marketUrl;
            }

            @Override
            public Class<? extends Annotation> annotationType()
            {
                return null;
            }
        } );
    }

    @AfterEach
    void shutdown()
    {
        this.server.stop( 0 );
    }

    @Test
    void test_403()
    {
        testStatus( 403 );
    }

    @Test
    void test_404()
    {
        testStatus( 404 );
    }

    @Test
    void test_500()
    {
        testStatus( 500 );
    }


    @Test
    void test_200()
        throws Exception
    {
        this.server.createContext( "/", exchange -> {
            exchange.sendResponseHeaders( 200, 0 );
            try (final InputStream is = getClass().getResourceAsStream( "empty_result.json" ))
            {
                OutputStream os = exchange.getResponseBody();
                os.write( is.readAllBytes() );
            }
            exchange.close();
        } );
        provider.search( List.of(), "latest", 0, 0 );
    }

    @Test
    void test_200_gzip()
        throws Exception
    {
        this.server.createContext( "/", exchange -> {
            final byte[] bytes;
            try (final InputStream is = getClass().getResourceAsStream(
                "empty_result.json" ); final ByteArrayOutputStream out = new ByteArrayOutputStream(); final GZIPOutputStream gzip = new GZIPOutputStream(
                out ))
            {
                gzip.write( is.readAllBytes() );
                gzip.finish();
                bytes = out.toByteArray();
            }

            exchange.getResponseHeaders().add( "Content-Encoding", "gzip" );
            exchange.sendResponseHeaders( 200, 0 );
            OutputStream os = exchange.getResponseBody();
            os.write( bytes );
            exchange.close();
        } );
        provider.search( List.of(), "latest", 0, 0 );
    }

    private void testStatus( final int code )
    {
        prepareResponse( code );

        try
        {
            provider.search( List.of(), "latest", 0, 0 );
        }
        catch ( MarketException e )
        {
            e.printStackTrace();
            assertEquals( code, e.getHttpErrorCode() );
        }
    }

    private void prepareResponse( final int code )
    {
        this.server.createContext( "/", exchange -> {
            exchange.sendResponseHeaders( code, 0 );
            exchange.close();
        } );
    }
}
