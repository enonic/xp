package com.enonic.xp.impl.server.rest;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.io.ByteSource;
import com.sun.net.httpserver.HttpServer;

import com.enonic.xp.event.Event;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ApplicationLoaderTest
{
    private static final long DEFAULT_MAX_SIZE = 1L << 30;

    private static final int DEFAULT_MAX_REDIRECTS = 5;

    private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 10_000;

    private static final int DEFAULT_READ_TIMEOUT_MILLIS = 60_000;

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

        final ByteSource byteSource = loader( appUrl + "*", false ).load( appUrl, null, eventListener );

        verify( eventListener ).accept( notNull() );
        assertTrue( byteSource.contentEquals( ByteSource.wrap( bytes ) ) );
    }

    @Test
    void load_with_sha512()
        throws Exception
    {
        final byte[] bytes = "this is a test".getBytes( StandardCharsets.UTF_8 );

        this.server.createContext( "/", exchange -> {

            exchange.sendResponseHeaders( 200, 0 );
            OutputStream os = exchange.getResponseBody();
            os.write( bytes );
            exchange.close();
        } );

        final ByteSource byteSource = loader( appUrl + "*", true ).load( appUrl, "7d0a8468ed220400c0b8e6f335baa7e070ce880a37e2ac5995b9a97b809026de626da636ac7365249bb974c719edf543b52ed286646f437dc7f810cc2068375c", eventListener );

        verify( eventListener ).accept( notNull() );
        assertTrue( byteSource.contentEquals( ByteSource.wrap( bytes ) ) );
    }

    @Test
    void load_with_sha512_wrong()
    {
        final byte[] bytes = "this is a test".getBytes( StandardCharsets.UTF_8 );

        this.server.createContext( "/", exchange -> {

            exchange.sendResponseHeaders( 200, 0 );
            OutputStream os = exchange.getResponseBody();
            os.write( bytes );
            exchange.close();
        } );

        final ApplicationLoader loader = loader( appUrl + "*", true );

        assertThrows( WebException.class, () -> loader.load( appUrl, "0d0a8468ed220400c0b8e6f335baa7e070ce880a37e2ac5995b9a97b809026de626da636ac7365249bb974c719edf543b52ed286646f437dc7f810cc2068375c", eventListener ) );
    }

    @ParameterizedTest
    @ValueSource(ints = {301, 302, 303, 307, 308})
    void load_follows_redirect_within_allowlist( final int status )
        throws Exception
    {
        final byte[] bytes = "this is a test".getBytes( StandardCharsets.UTF_8 );

        this.server.createContext( "/old", exchange -> {
            exchange.getResponseHeaders().add( "Location", "/new/app.jar" );
            exchange.sendResponseHeaders( status, -1 );
            exchange.close();
        } );
        this.server.createContext( "/new", exchange -> {
            exchange.sendResponseHeaders( 200, 0 );
            exchange.getResponseBody().write( bytes );
            exchange.close();
        } );

        final ByteSource byteSource = loader( appUrl + "*", false ).load( appUrl + "/old", null, eventListener );

        assertTrue( byteSource.contentEquals( ByteSource.wrap( bytes ) ) );
        verify( eventListener, atLeastOnce() ).accept(
            argThat( event -> ( appUrl + "/old" ).equals( event.getValue( "applicationUrl" ).orElse( null ) ) ) );
    }

    @Test
    void load_rejects_invalid_redirect_location()
    {
        this.server.createContext( "/old", exchange -> {
            exchange.getResponseHeaders().add( "Location", "http://exa mple.com/app.jar" );
            exchange.sendResponseHeaders( 302, -1 );
            exchange.close();
        } );

        final ApplicationLoader loader = loader( appUrl + "*", false );

        assertThatThrownBy( () -> loader.load( appUrl + "/old", null, eventListener ) ).isInstanceOfSatisfying( WebException.class,
                                                                                                                e -> assertThat(
                                                                                                                    e.getStatus() ).isEqualTo(
                                                                                                                    HttpStatus.BAD_REQUEST ) );
        verifyNoInteractions( eventListener );
    }

    @Test
    void load_from_file_url( @TempDir final Path tempDir )
        throws Exception
    {
        final byte[] bytes = "this is a test".getBytes( StandardCharsets.UTF_8 );
        final Path file = Files.write( tempDir.resolve( "app.jar" ), bytes );

        final ByteSource byteSource = loader( "file:*", false ).load( file.toUri().toString(), null, eventListener );

        verify( eventListener ).accept( notNull() );
        assertTrue( byteSource.contentEquals( ByteSource.wrap( bytes ) ) );
    }

    @Test
    void rejects_max_size_out_of_range()
    {
        assertThrows( IllegalArgumentException.class,
                      () -> new ApplicationLoader( "", false, 0, DEFAULT_MAX_REDIRECTS, DEFAULT_CONNECT_TIMEOUT_MILLIS,
                                                   DEFAULT_READ_TIMEOUT_MILLIS ) );
        assertThrows( IllegalArgumentException.class,
                      () -> new ApplicationLoader( "", false, Long.MAX_VALUE, DEFAULT_MAX_REDIRECTS,
                                                   DEFAULT_CONNECT_TIMEOUT_MILLIS, DEFAULT_READ_TIMEOUT_MILLIS ) );
        assertThrows( IllegalArgumentException.class, () -> new ApplicationLoader( "", false, 1, -1, 1, 1 ) );
        assertThrows( IllegalArgumentException.class, () -> new ApplicationLoader( "", false, 1, 1, 0, 1 ) );
        assertThrows( IllegalArgumentException.class, () -> new ApplicationLoader( "", false, 1, 1, 1, 0 ) );
    }

    @Test
    void load_rejects_redirect_outside_allowlist()
    {
        this.server.createContext( "/old", exchange -> {
            exchange.getResponseHeaders().add( "Location", "http://other.invalid/app.jar" );
            exchange.sendResponseHeaders( 302, -1 );
            exchange.close();
        } );

        final ApplicationLoader loader = loader( appUrl + "*", false );

        assertThatThrownBy( () -> loader.load( appUrl + "/old", null, eventListener ) ).isInstanceOfSatisfying(
            WebException.class, e -> assertThat( e.getStatus() ).isEqualTo( HttpStatus.CONFLICT ) );
        verifyNoInteractions( eventListener );
    }

    @Test
    void load_rejects_too_many_redirects()
    {
        this.server.createContext( "/loop", exchange -> {
            exchange.getResponseHeaders().add( "Location", "/loop" );
            exchange.sendResponseHeaders( 302, -1 );
            exchange.close();
        } );

        final ApplicationLoader loader = loader( appUrl + "*", false );

        assertThatThrownBy( () -> loader.load( appUrl + "/loop", null, eventListener ) ).isInstanceOfSatisfying(
            WebException.class, e -> assertThat( e.getStatus() ).isEqualTo( HttpStatus.BAD_REQUEST ) );
        verifyNoInteractions( eventListener );
    }

    @Test
    void load_rejects_oversized_application()
    {
        final byte[] bytes = "this is a test".getBytes( StandardCharsets.UTF_8 );

        this.server.createContext( "/", exchange -> {
            exchange.sendResponseHeaders( 200, 0 );
            exchange.getResponseBody().write( bytes );
            exchange.close();
        } );

        final ApplicationLoader loader = loader( appUrl + "*", false, bytes.length - 1 );

        assertThatThrownBy( () -> loader.load( appUrl, null, eventListener ) ).isInstanceOfSatisfying(
            WebException.class, e -> assertThat( e.getStatus() ).isEqualTo( HttpStatus.BAD_REQUEST ) );
    }

    @Test
    void load_rejects_oversized_content_length()
    {
        final byte[] bytes = "this is a test".getBytes( StandardCharsets.UTF_8 );

        this.server.createContext( "/", exchange -> {
            exchange.sendResponseHeaders( 200, bytes.length );
            exchange.getResponseBody().write( bytes );
            exchange.close();
        } );

        final ApplicationLoader loader = loader( appUrl + "*", false, bytes.length - 1 );

        assertThatThrownBy( () -> loader.load( appUrl, null, eventListener ) ).isInstanceOfSatisfying(
            WebException.class, e -> assertThat( e.getStatus() ).isEqualTo( HttpStatus.BAD_REQUEST ) );
        verifyNoInteractions( eventListener );
    }

    @Test
    void load_rejects_url_outside_allowlist()
    {
        final ApplicationLoader loader = loader( "https://allowed.example/*", false );

        assertThatThrownBy( () -> loader.load( appUrl, null, eventListener ) ).isInstanceOfSatisfying( WebException.class,
                                                                                                       e -> assertThat( e.getStatus() ).isEqualTo(
                                                                                                           HttpStatus.CONFLICT ) );
        verifyNoInteractions( eventListener );
    }

    @Test
    void load_rejects_when_empty_allowlist()
    {
        final ApplicationLoader loader = loader( "", false );

        assertThatThrownBy( () -> loader.load( appUrl, null, eventListener ) ).isInstanceOfSatisfying( WebException.class,
                                                                                                       e -> assertThat( e.getStatus() ).isEqualTo(
                                                                                                           HttpStatus.CONFLICT ) );
        verifyNoInteractions( eventListener );
    }

    @Test
    void load_rejects_when_checksum_required_but_missing()
    {
        final ApplicationLoader loader = loader( appUrl + "*", true );

        assertThatThrownBy( () -> loader.load( appUrl, null, eventListener ) ).isInstanceOfSatisfying( WebException.class,
                                                                                                       e -> assertThat( e.getStatus() ).isEqualTo(
                                                                                                           HttpStatus.CONFLICT ) );
        verifyNoInteractions( eventListener );
    }

    @Test
    void load_rejects_when_checksum_required_but_blank()
    {
        final ApplicationLoader loader = loader( appUrl + "*", true );

        assertThatThrownBy( () -> loader.load( appUrl, "   ", eventListener ) ).isInstanceOfSatisfying( WebException.class,
                                                                                                        e -> assertThat( e.getStatus() ).isEqualTo(
                                                                                                            HttpStatus.CONFLICT ) );
        verifyNoInteractions( eventListener );
    }

    @Test
    void load_rejects_invalid_sha512_hex_with_400()
    {
        final ApplicationLoader loader = loader( "https://*", true );

        assertThatThrownBy( () -> loader.load( "https://example.com/foo", "not-a-hex-string", eventListener ) ).isInstanceOfSatisfying(
            WebException.class, e -> assertThat( e.getStatus() ).isEqualTo( HttpStatus.BAD_REQUEST ) );
    }

    @Test
    void load_rejects_malformed_url_with_400()
    {
        final ApplicationLoader loader = loader( "xyz://*", false );

        assertThatThrownBy( () -> loader.load( "xyz://example.com/foo", null, eventListener ) ).isInstanceOfSatisfying( WebException.class,
                                                                                                                       e -> assertThat(
                                                                                                                           e.getStatus() ).isEqualTo(
                                                                                                                           HttpStatus.BAD_REQUEST ) );
    }

    @Test
    void load_rejects_unparseable_uri_with_400()
    {
        final ApplicationLoader loader = loader( "https://*", false );

        assertThatThrownBy( () -> loader.load( "https://exa mple.com", null, eventListener ) ).isInstanceOfSatisfying( WebException.class,
                                                                                                                      e -> assertThat(
                                                                                                                          e.getStatus() ).isEqualTo(
                                                                                                                          HttpStatus.BAD_REQUEST ) );
    }

    @Test
    void load_allows_when_url_in_allowlist_and_checksum_not_required()
        throws Exception
    {
        final byte[] bytes = "this is a test".getBytes( StandardCharsets.UTF_8 );
        this.server.createContext( "/", exchange -> {
            exchange.sendResponseHeaders( 200, 0 );
            final OutputStream os = exchange.getResponseBody();
            os.write( bytes );
            exchange.close();
        } );

        final ApplicationLoader loader = loader( appUrl + "*", false );
        final ByteSource result = loader.load( appUrl, null, eventListener );

        assertTrue( result.contentEquals( ByteSource.wrap( bytes ) ) );
    }

    private static ApplicationLoader loader( final String allowedUrls, final boolean requireChecksum )
    {
        return new ApplicationLoader( allowedUrls, requireChecksum, DEFAULT_MAX_SIZE, DEFAULT_MAX_REDIRECTS,
                                      DEFAULT_CONNECT_TIMEOUT_MILLIS, DEFAULT_READ_TIMEOUT_MILLIS );
    }

    private static ApplicationLoader loader( final String allowedUrls, final boolean requireChecksum, final long maxSize )
    {
        return new ApplicationLoader( allowedUrls, requireChecksum, maxSize, DEFAULT_MAX_REDIRECTS, DEFAULT_CONNECT_TIMEOUT_MILLIS,
                                      DEFAULT_READ_TIMEOUT_MILLIS );
    }
}
