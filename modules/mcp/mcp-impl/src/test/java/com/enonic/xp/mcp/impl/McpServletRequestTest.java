package com.enonic.xp.mcp.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import jakarta.servlet.ReadListener;
import jakarta.servlet.http.HttpServletRequest;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@NullMarked
class McpServletRequestTest
{
    @Test
    void replays_utf8_body_with_byte_lengths_and_independent_readers()
        throws IOException
    {
        final var original = mock( HttpServletRequest.class );
        final var body = "{\"message\":\"Héllo 世界 🌍\"}";
        final var request = new McpServletRequest( original, body );

        assertThat( request.getCharacterEncoding() ).isEqualTo( "UTF-8" );
        assertThat( request.getContentLength() ).isEqualTo( body.getBytes( UTF_8 ).length ).isGreaterThan( body.length() );
        assertThat( request.getContentLengthLong() ).isEqualTo( body.getBytes( UTF_8 ).length );
        assertThat( request.getInputStream().readAllBytes() ).isEqualTo( body.getBytes( UTF_8 ) );
        assertThat( request.getReader().readLine() ).isEqualTo( body );
        assertThat( request.getReader().readLine() ).isEqualTo( body );
    }

    @Test
    void preserves_original_uri_headers_and_connector()
    {
        final var original = mock( HttpServletRequest.class );
        when( original.getRequestURI() ).thenReturn( "/api/system:mcp" );
        when( original.getHeader( "Mcp-Method" ) ).thenReturn( "tools/list" );
        when( original.getHeaders( "Mcp-Method" ) ).thenReturn( Collections.enumeration( List.of( "tools/list", "tools/call" ) ) );
        when( original.getAttribute( "connector" ) ).thenReturn( "web" );
        final var request = new McpServletRequest( original, "{}" );

        assertThat( request.getRequestURI() ).isEqualTo( "/api/system:mcp" );
        assertThat( request.getHeader( "Mcp-Method" ) ).isEqualTo( "tools/list" );
        assertThat( Collections.list( request.getHeaders( "Mcp-Method" ) ) ).containsExactly( "tools/list", "tools/call" );
        assertThat( request.getAttribute( "connector" ) ).isEqualTo( "web" );
    }

    @Test
    void replays_an_already_consumed_request()
        throws IOException
    {
        final var original = new McpServletRequest( mock( HttpServletRequest.class ), "cached" );
        final var consumed = original.getInputStream();
        assertThat( consumed.readAllBytes() ).isEqualTo( "cached".getBytes( UTF_8 ) );
        final var raw = mock( HttpServletRequest.class );
        when( raw.getInputStream() ).thenReturn( consumed );

        final var request = new McpServletRequest( raw, "cached" );

        assertThat( raw.getInputStream().read() ).isEqualTo( -1 );
        assertThat( request.getReader().readLine() ).isEqualTo( "cached" );
    }

    @Test
    void exposes_blocking_stream_state_and_empty_body()
        throws IOException
    {
        final var stream = new McpServletRequest( mock( HttpServletRequest.class ), "x" ).getInputStream();
        assertThat( stream.isReady() ).isTrue();
        assertThat( stream.isFinished() ).isFalse();
        assertThat( stream.read() ).isEqualTo( 'x' );
        assertThat( stream.isFinished() ).isTrue();
        assertThat( stream.read() ).isEqualTo( -1 );
        assertThatThrownBy( () -> stream.setReadListener( mock( ReadListener.class ) ) )
            .isInstanceOf( UnsupportedOperationException.class );

        final var empty = new McpServletRequest( mock( HttpServletRequest.class ), "" );
        assertThat( empty.getContentLengthLong() ).isZero();
        assertThat( empty.getInputStream().isFinished() ).isTrue();
        assertThat( empty.getReader().readLine() ).isNull();
    }
}
