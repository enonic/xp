package com.enonic.xp.mcp.impl;

import java.util.List;
import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.testing.ScriptTestSupport;

import io.modelcontextprotocol.modern.McpSchema.CallToolOutcome;
import io.modelcontextprotocol.modern.McpSchema.CallToolRequest;
import io.modelcontextprotocol.modern.McpSchema.CallToolResult;
import io.modelcontextprotocol.modern.McpSchema.ResultType;
import io.modelcontextprotocol.modern.McpSchema.TextContent;
import io.modelcontextprotocol.modern.McpSchema.Tool;
import io.modelcontextprotocol.modern.server.McpRequestContext;
import io.modelcontextprotocol.modern.server.McpSyncNotifier;
import io.modelcontextprotocol.modern.server.McpSyncResponse;
import io.modelcontextprotocol.modern.server.feature.McpSyncTool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@NullMarked
class McpProgressControllerTest
    extends ScriptTestSupport
{
    private final McpSyncNotifier notifier = mock( McpSyncNotifier.class );

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5})
    void streams_all_helper_overloads_from_javascript( final int steps )
    {
        final var context = mock( McpRequestContext.class );
        when( context.progressToken() ).thenReturn( "fixture-token" );
        final var response = binding().handler().call( context, request( Map.of( "steps", steps ) ) );

        assertThat( response ).isInstanceOf( McpSyncResponse.Streaming.class );
        verifyNoInteractions( notifier );
        final var outcome = ( (McpSyncResponse.Streaming<CallToolOutcome>) response ).body().run( notifier );

        assertResult( outcome, steps, true );
        final var order = inOrder( notifier );
        order.verify( notifier ).progress( 0, null, null );
        order.verify( notifier ).progress( 0.25, null, "Starting" );
        order.verify( notifier ).progress( 0.5, (double) steps, null );
        for ( int step = 1; step <= steps; step++ )
        {
            order.verify( notifier ).progress( step, (double) steps, "Step " + step );
        }
        verifyNoMoreInteractions( notifier );
    }

    @Test
    void no_token_executes_javascript_with_noop_helper_and_json_result()
    {
        final var response = binding().handler().call( mock( McpRequestContext.class ), request( Map.of( "steps", 3 ) ) );

        assertThat( response ).isInstanceOf( McpSyncResponse.Result.class );
        assertResult( ( (McpSyncResponse.Result<CallToolOutcome>) response ).result(), 3, false );
        verifyNoInteractions( notifier );
    }

    @Test
    void fixture_accepts_omitted_third_argument()
    {
        final var binding = McpToolAdapter.bind( tool(), ( name, arguments ) -> runScript( "/mcp/progress/progress.js" )
            .executeBound( exports -> exports.executeMethod( "execute", name, arguments ).getMap() ) );
        final var response = binding.handler().call( mock( McpRequestContext.class ), request( Map.of( "steps", 1 ) ) );

        assertThat( response ).isInstanceOf( McpSyncResponse.Result.class );
        assertResult( ( (McpSyncResponse.Result<CallToolOutcome>) response ).result(), 1, false );
        verifyNoInteractions( notifier );
    }

    @Test
    void validates_bounded_steps_before_executing_controller()
    {
        final var binding = McpToolAdapter.bind( tool(), ( name, arguments ) -> {
            throw new AssertionError( "Invalid input reached controller" );
        } );
        final var context = mock( McpRequestContext.class );
        when( context.progressToken() ).thenReturn( "fixture-token" );
        for ( var arguments : List.<Map<String, @Nullable Object>>of( Map.of(), Map.of( "steps", 0 ), Map.of( "steps", 6 ),
                                                                      Map.of( "steps", 1.5 ), Map.of( "steps", "2" ),
                                                                      Map.of( "steps", 2, "extra", true ) ) )
        {
            final var response = binding.handler().call( context, request( arguments ) );
            assertThat( response ).isInstanceOf( McpSyncResponse.Result.class );
            final var outcome = ( (McpSyncResponse.Result<CallToolOutcome>) response ).result();
            assertThat( outcome ).isInstanceOf( CallToolResult.class );
            final var result = (CallToolResult) outcome;
            assertThat( result.isError() ).isTrue();
            assertThat( result.content() ).singleElement().isInstanceOfSatisfying( TextContent.class,
                text -> assertThat( text.text() ).startsWith( "Invalid MCP tool arguments:" ) );
        }
        verifyNoInteractions( notifier );
    }

    private McpSyncTool binding()
    {
        return McpToolAdapter.bind( tool(), ( name, arguments ) -> runScript( "/mcp/progress/progress.js" ).executeBound(
            exports -> exports.executeMethod( "execute", name, arguments, McpInvocationScope.current() ).getMap() ) );
    }

    private Tool tool()
    {
        final var key = DescriptorKey.from( "myapplication:progress" );
        final var source = loadResource( ResourceKey.from( key.getApplicationKey(), "/mcp/progress/progress.yaml" ) ).readString();
        final var group = YmlMcpDescriptorParser.parse( source, key );
        assertThat( group.tools() ).hasSize( 1 );
        return group.tools().getFirst();
    }

    private static CallToolRequest request( final Map<String, @Nullable Object> arguments )
    {
        return CallToolRequest.builder( "progress" ).arguments( arguments ).build();
    }

    private static void assertResult( final CallToolOutcome outcome, final int steps, final boolean supported )
    {
        assertThat( outcome ).isInstanceOf( CallToolResult.class );
        final var result = (CallToolResult) outcome;
        assertThat( result.isError() ).isFalse();
        assertThat( result.resultType() ).isEqualTo( ResultType.COMPLETE );
        assertThat( result.content() ).singleElement().isInstanceOfSatisfying( TextContent.class,
            text -> assertThat( text.text() ).isEqualTo( "Completed " + steps + " steps" ) );
        assertThat( result.structuredContent() ).isInstanceOf( Map.class );
        final var content = (Map<?, ?>) result.structuredContent();
        assertThat( content ).hasSize( 2 );
        assertThat( content.get( "completedSteps" ) ).isInstanceOfSatisfying( Number.class,
            number -> assertThat( number.doubleValue() ).isEqualTo( steps ) );
        assertThat( content.get( "progressSupported" ) ).isEqualTo( supported );
    }
}
