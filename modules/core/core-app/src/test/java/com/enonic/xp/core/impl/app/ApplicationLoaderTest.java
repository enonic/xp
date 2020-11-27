package com.enonic.xp.core.impl.app;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.io.ByteSource;
import com.sun.net.httpserver.HttpServer;

import com.enonic.xp.event.Event;
import com.enonic.xp.util.HexEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApplicationLoaderTest
{
    private HttpServer server;

    private String appUrl;

    @Mock
    Consumer<Event> eventListener;

    @BeforeEach
    void setUp()
        throws Exception
    {
        server = HttpServer.create( new InetSocketAddress( 0 ), 0 );
        server.start();
        appUrl = "http://localhost:" + this.server.getAddress().getPort();
    }

    @AfterEach
    void shutdown()
    {
        this.server.stop( 0 );
    }

    @Test
    void load()
        throws Exception
    {
        final byte[] bytes = "this is a test".getBytes( StandardCharsets.UTF_8 );

        this.server.createContext( "/", exchange -> {

            exchange.sendResponseHeaders( 200, 0 );
            OutputStream os = exchange.getResponseBody();
            os.write( bytes );
            exchange.close();
        } );

        final ByteSource byteSource = new ApplicationLoader( eventListener ).load( new URL( appUrl ), null );

        verify( eventListener ).accept( notNull() );
        assertTrue( byteSource.contentEquals( ByteSource.wrap( bytes ) ) );
    }

    @Test
    void load_with_sha512()
        throws Exception
    {
        final byte[] bytes = "this is a test".getBytes( StandardCharsets.UTF_8 );
        final byte[] sha512 = HexEncoder.fromHex(
            "7d0a8468ed220400c0b8e6f335baa7e070ce880a37e2ac5995b9a97b809026de626da636ac7365249bb974c719edf543b52ed286646f437dc7f810cc2068375c" );

        this.server.createContext( "/", exchange -> {

            exchange.sendResponseHeaders( 200, 0 );
            OutputStream os = exchange.getResponseBody();
            os.write( bytes );
            exchange.close();
        } );

        final ByteSource byteSource = new ApplicationLoader( eventListener ).load( new URL( appUrl ), sha512 );

        verify( eventListener ).accept( notNull() );
        assertTrue( byteSource.contentEquals( ByteSource.wrap( bytes ) ) );
    }

    @Test
    void load_with_sha512_wrong()
    {
        final byte[] bytes = "this is a test".getBytes( StandardCharsets.UTF_8 );
        final byte[] sha512 = HexEncoder.fromHex(
            "0d0a8468ed220400c0b8e6f335baa7e070ce880a37e2ac5995b9a97b809026de626da636ac7365249bb974c719edf543b52ed286646f437dc7f810cc2068375c" );

        this.server.createContext( "/", exchange -> {

            exchange.sendResponseHeaders( 200, 0 );
            OutputStream os = exchange.getResponseBody();
            os.write( bytes );
            exchange.close();
        } );

        assertThrows( IllegalArgumentException.class, () -> new ApplicationLoader( eventListener ).load( new URL( appUrl ), sha512 ) );
    }
}
