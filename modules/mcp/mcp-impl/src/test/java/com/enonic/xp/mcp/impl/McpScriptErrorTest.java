package com.enonic.xp.mcp.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

import com.enonic.xp.mcp.McpInvocation;
import com.enonic.xp.testing.ScriptTestSupport;

import io.modelcontextprotocol.modern.McpSchema.CallToolOutcome;
import io.modelcontextprotocol.modern.McpSchema.CallToolRequest;
import io.modelcontextprotocol.modern.McpSchema.CallToolResult;
import io.modelcontextprotocol.modern.McpSchema.TextContent;
import io.modelcontextprotocol.modern.McpSchema.Tool;
import io.modelcontextprotocol.modern.server.McpRequestContext;
import io.modelcontextprotocol.modern.server.McpSyncNotifier;
import io.modelcontextprotocol.modern.server.McpSyncResponse;
import io.modelcontextprotocol.modern.server.feature.McpSyncTool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@NullMarked
public class McpScriptErrorTest
    extends ScriptTestSupport
{
    private static final Pattern ERROR_ID = Pattern.compile( "MCP tool execution failed \\(error id ([0-9a-f]{8})\\)" );

    private static final List<String> SECRETS = List.of( "secret-js", "secret-iae", "secret-io", "secret-so", "/etc/x" );

    private final Logger log = mock( Logger.class );

    private final AtomicReference<McpInvocation> invocation = new AtomicReference<>();

    public void fail( final String kind )
        throws IOException
    {
        switch ( kind )
        {
            case "argument" -> throw new IllegalArgumentException( "secret-iae /etc/x" );
            case "checked" -> throw new IOException( "secret-io /etc/x/xp-secret-file" );
            case "overflow" -> throw new StackOverflowError( "secret-so" );
            default -> throw new IllegalStateException( "Unknown failure kind " + kind );
        }
    }

    @ParameterizedTest
    @CsvSource({"script, secret-js", "argument, secret-iae", "checked, secret-io", "overflow, secret-so"})
    void thrown_failures_become_generic_tool_errors_with_logged_error_id( final String kind, final String loggedDetail )
    {
        final var response = binding().handler().call( mock( McpRequestContext.class ), request( kind ) );

        assertThat( response ).isInstanceOf( McpSyncResponse.Result.class );
        assertGenericError( ( (McpSyncResponse.Result<CallToolOutcome>) response ).result(), loggedDetail );
    }

    @Test
    void progress_stream_finishes_with_generic_tool_error_and_deactivated_helper()
    {
        final var context = mock( McpRequestContext.class );
        when( context.progressToken() ).thenReturn( "failing-token" );
        final var notifier = mock( McpSyncNotifier.class );

        final var response = binding().handler().call( context, request( "script" ) );

        assertThat( response ).isInstanceOf( McpSyncResponse.Streaming.class );
        assertGenericError( ( (McpSyncResponse.Streaming<CallToolOutcome>) response ).body().run( notifier ), "secret-js" );
        verify( notifier ).progress( 0, null, "Started" );
        assertThat( invocation.get().isProgressSupported() ).isFalse();
    }

    @Test
    void returned_error_reaches_client_unchanged()
    {
        final var response = binding().handler().call( mock( McpRequestContext.class ), request( "returned" ) );

        final var outcome = ( (McpSyncResponse.Result<CallToolOutcome>) response ).result();
        assertThat( outcome ).isInstanceOfSatisfying( CallToolResult.class, result -> {
            assertThat( result.isError() ).isTrue();
            assertThat( result.content() ).singleElement().isInstanceOfSatisfying( TextContent.class,
                text -> assertThat( text.text() ).isEqualTo( "Visible failure for the model" ) );
        } );
        verifyNoInteractions( log );
    }

    private McpSyncTool binding()
    {
        final var schema = Map.of( "type", "object", "properties", Map.of( "kind", Map.of( "type", "string" ) ), "required",
                                   List.of( "kind" ) );
        return McpToolAdapter.bind( Tool.builder( "failing", schema ).build(), ( name, arguments ) -> {
            invocation.set( McpInvocationScope.current() );
            return runScript( "/mcp/failing/failing.js" ).executeBound(
                exports -> exports.executeMethod( "execute", name, arguments, McpInvocationScope.current() ).getMap() );
        }, log );
    }

    private void assertGenericError( final CallToolOutcome outcome, final String loggedDetail )
    {
        assertThat( outcome ).isInstanceOf( CallToolResult.class );
        final var result = (CallToolResult) outcome;
        assertThat( result.isError() ).isTrue();
        assertThat( result.content() ).singleElement().isInstanceOf( TextContent.class );
        final var text = ( (TextContent) result.content().getFirst() ).text();
        assertThat( text ).doesNotContain( SECRETS.toArray( String[]::new ) );
        final var matcher = ERROR_ID.matcher( text );
        assertThat( matcher.matches() ).as( text ).isTrue();
        final var failure = ArgumentCaptor.forClass( Throwable.class );
        verify( log ).warn( eq( "MCP tool {} execution failed (error id {})" ), eq( "failing" ), eq( matcher.group( 1 ) ),
                            failure.capture() );
        assertThat( failure.getValue() ).hasStackTraceContaining( loggedDetail );
    }

    private static CallToolRequest request( final String kind )
    {
        return CallToolRequest.builder( "failing" ).arguments( Map.of( "kind", kind ) ).build();
    }
}
