package com.enonic.xp.mcp.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.mcp.McpInvocation;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;

import io.modelcontextprotocol.modern.McpSchema.CallToolOutcome;
import io.modelcontextprotocol.modern.McpSchema.CallToolResult;
import io.modelcontextprotocol.modern.server.McpRequestContext;
import io.modelcontextprotocol.modern.server.McpSyncNotifier;
import io.modelcontextprotocol.modern.server.McpSyncResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@NullMarked
class McpToolExecutorImplTest
{
    private static final DescriptorKey KEY = DescriptorKey.from( "contributing.app:group" );

    private static final ResourceKey CONTROLLER = ResourceKey.from( "contributing.app:/mcp/group/group.js" );

    private static final ApplicationKey CALLER = ApplicationKey.from( "caller.app" );

    private static final ScopedValue<Boolean> BOUND = ScopedValue.newInstance();

    private final McpDescriptorServiceImpl descriptors = mock( McpDescriptorServiceImpl.class );

    private final PortalScriptService scripts = mock( PortalScriptService.class );

    private final ScriptExports exports = mock( ScriptExports.class );

    private final ScriptExports boundExports = mock( ScriptExports.class );

    private final ScriptValue value = mock( ScriptValue.class );

    private final Map<String, @Nullable Object> arguments = new LinkedHashMap<>();

    private final Map<String, @Nullable Object> result = Map.of( "content", List.of( Map.of( "type", "text", "text", "done" ) ) );

    private final McpToolExecutorImpl executor = new McpToolExecutorImpl( descriptors, scripts );

    @BeforeEach
    void setUp()
    {
        arguments.put( "message", "hello" );
        arguments.put( "nullable", null );
        when( descriptors.getControllerResourceKey( KEY ) ).thenReturn( CONTROLLER );
        when( scripts.hasScript( CONTROLLER ) ).thenReturn( true );
        when( scripts.execute( CONTROLLER ) ).thenReturn( exports );
        when( exports.executeBound( any() ) ).thenAnswer( invocation -> {
            assertToolRequest();
            final Function<ScriptExports, Map<String, @Nullable Object>> work = invocation.getArgument( 0 );
            return ScopedValue.where( BOUND, true ).call( () -> work.apply( boundExports ) );
        } );
        when( boundExports.getScript() ).thenReturn( CONTROLLER );
        when( boundExports.hasMethod( "execute" ) ).thenReturn( true );
        when( boundExports.executeMethod( eq( "execute" ), eq( "local_name" ), same( arguments ), any( McpInvocation.class ) ) )
            .thenAnswer( invocation -> {
                assertThat( BOUND.isBound() ).isTrue();
                assertToolRequest();
                assertThat( invocation.<McpInvocation>getArgument( 3 ) ).isSameAs( McpInvocationScope.current() );
                return value;
            } );
        when( value.isObject() ).thenReturn( true );
        when( value.getMap() ).thenAnswer( invocation -> {
            assertThat( BOUND.isBound() ).isTrue();
            assertToolRequest();
            return result;
        } );
    }

    @Test
    void invokes_bound_exports_and_converts_result_within_execute_bound()
    {
        inRequest( () -> assertThat( execute() ).isSameAs( result ) );

        verify( descriptors ).getControllerResourceKey( KEY );
        verify( scripts ).hasScript( CONTROLLER );
        verify( scripts ).execute( CONTROLLER );
        verify( exports ).executeBound( any() );
        verify( exports, never() ).hasMethod( any() );
        verify( exports, never() ).executeMethod( any(), any(), any(), any() );
        verify( boundExports ).executeMethod( eq( "execute" ), eq( "local_name" ), same( arguments ), same( McpInvocationScope.current() ) );
        verify( value ).getMap();
        assertThat( BOUND.isBound() ).isFalse();
        assertThat( arguments ).containsEntry( "message", "hello" ).containsEntry( "nullable", null );
    }

    @Test
    void passes_active_invocation_as_third_controller_argument()
    {
        final var context = mock( McpRequestContext.class );
        when( context.progressToken() ).thenReturn( "client-token" );
        final var notifier = mock( McpSyncNotifier.class );
        final var helper = new AtomicReference<McpInvocation>();
        doAnswer( invocation -> {
            final McpInvocation current = invocation.getArgument( 3 );
            assertThat( current ).isSameAs( McpInvocationScope.current() );
            assertThat( current.isProgressSupported() ).isTrue();
            assertThat( BOUND.isBound() ).isTrue();
            assertToolRequest();
            helper.set( current );
            current.progress( 1, "controller" );
            return value;
        } ).when( boundExports ).executeMethod( eq( "execute" ), eq( "local_name" ), same( arguments ), any( McpInvocation.class ) );
        inRequest( () -> {
            final var response = McpInvocationScope.respond( context, () -> {
                assertThat( execute() ).isSameAs( result );
                return CallToolResult.builder().build();
            } );
            assertThat( ( (McpSyncResponse.Streaming<CallToolOutcome>) response ).body().run( notifier ) ).isNotNull();
        } );
        verify( notifier ).progress( 1, null, "controller" );
        assertThat( helper.get().isProgressSupported() ).isFalse();
    }

    @Test
    void missing_script_reports_controller_and_restores_request()
    {
        when( scripts.hasScript( CONTROLLER ) ).thenReturn( false );

        inRequest( () -> assertThatThrownBy( this::execute ).isInstanceOf( IllegalArgumentException.class )
            .hasMessage( "Missing MCP controller " + CONTROLLER ) );

        verify( scripts, never() ).execute( any() );
        verifyNoInteractions( exports, boundExports, value );
    }

    @Test
    void missing_execute_export_reports_controller_and_restores_request()
    {
        when( boundExports.hasMethod( "execute" ) ).thenReturn( false );

        inRequest( () -> assertThatThrownBy( this::execute ).isInstanceOf( IllegalArgumentException.class )
            .hasMessage( "MCP controller " + CONTROLLER + " must export execute(name, args)" ) );

        verify( boundExports, never() ).executeMethod( any(), any(), any(), any() );
        verifyNoInteractions( value );
    }

    @Test
    void rejects_null_return_and_restores_request()
    {
        doReturn( null ).when( boundExports ).executeMethod( eq( "execute" ), eq( "local_name" ), same( arguments ), any( McpInvocation.class ) );

        assertInvalidReturn();
        verifyNoInteractions( value );
    }

    @Test
    void rejects_scalar_return_and_restores_request()
    {
        when( value.isObject() ).thenReturn( false );
        when( value.isValue() ).thenReturn( true );

        assertInvalidReturn();
        verify( value, never() ).getMap();
    }

    @Test
    void rejects_array_return_and_restores_request()
    {
        when( value.isArray() ).thenReturn( true );

        assertInvalidReturn();
        verify( value, never() ).getMap();
    }

    @Test
    void rejects_function_return_and_restores_request()
    {
        when( value.isFunction() ).thenReturn( true );

        assertInvalidReturn();
        verify( value, never() ).getMap();
    }

    @Test
    void propagates_script_exception_and_restores_request()
    {
        final var failure = new IllegalStateException( "script failure" );
        doAnswer( invocation -> {
            assertThat( BOUND.isBound() ).isTrue();
            assertToolRequest();
            throw failure;
        } ).when( boundExports ).executeMethod( eq( "execute" ), eq( "local_name" ), same( arguments ), any( McpInvocation.class ) );

        inRequest( () -> assertThatThrownBy( this::execute ).isSameAs( failure ) );

        verify( value, never() ).getMap();
        assertThat( BOUND.isBound() ).isFalse();
    }

    @Test
    void propagates_script_loading_exception_and_restores_request()
    {
        final var failure = new IllegalStateException( "script loading failure" );
        doAnswer( invocation -> {
            assertToolRequest();
            throw failure;
        } ).when( scripts ).execute( CONTROLLER );

        inRequest( () -> assertThatThrownBy( this::execute ).isSameAs( failure ) );

        verifyNoInteractions( exports, boundExports, value );
    }

    @Test
    void propagates_conversion_exception_and_restores_request()
    {
        final var failure = new IllegalArgumentException( "conversion failure" );
        doAnswer( invocation -> {
            assertThat( BOUND.isBound() ).isTrue();
            assertToolRequest();
            throw failure;
        } ).when( value ).getMap();

        inRequest( () -> assertThatThrownBy( this::execute ).isSameAs( failure ) );

        assertThat( BOUND.isBound() ).isFalse();
    }

    @Test
    void creates_temporary_request_without_current_request_and_restores_after_success()
    {
        final var temporary = captureTemporaryRequest();
        assertThat( PortalRequestAccessor.get() ).isNull();

        assertThat( execute() ).isSameAs( result );

        assertThat( PortalRequestAccessor.get() ).isNull();
        assertThat( temporary.get() ).isNotNull();
        assertThat( temporary.get().getApplicationKey() ).isNull();
        verify( boundExports ).executeMethod( eq( "execute" ), eq( "local_name" ), same( arguments ), same( McpInvocationScope.current() ) );
    }

    @Test
    void restores_temporary_request_after_script_failure_without_current_request()
    {
        final var temporary = captureTemporaryRequest();
        final var failure = new IllegalStateException( "script failure" );
        doThrow( failure ).when( boundExports ).executeMethod( eq( "execute" ), eq( "local_name" ), same( arguments ), any( McpInvocation.class ) );
        assertThat( PortalRequestAccessor.get() ).isNull();

        assertThatThrownBy( this::execute ).isSameAs( failure );

        assertThat( PortalRequestAccessor.get() ).isNull();
        assertThat( temporary.get() ).isNotNull();
        assertThat( temporary.get().getApplicationKey() ).isNull();
        assertThat( BOUND.isBound() ).isFalse();
    }

    @Test
    void restores_request_with_no_application_after_success_and_failure()
    {
        final var request = new PortalRequest();
        PortalRequestAccessor.runWith( request, () -> {
            assertThat( execute() ).isSameAs( result );
            assertThat( request.getApplicationKey() ).isNull();
            assertThat( PortalRequestAccessor.get() ).isSameAs( request );

            when( scripts.hasScript( CONTROLLER ) ).thenReturn( false );
            assertThatThrownBy( this::execute ).isInstanceOf( IllegalArgumentException.class );
            assertThat( request.getApplicationKey() ).isNull();
            assertThat( PortalRequestAccessor.get() ).isSameAs( request );
        } );
        assertThat( PortalRequestAccessor.get() ).isNull();
    }

    private Map<String, @Nullable Object> execute()
    {
        return executor.execute( KEY, "local_name", arguments );
    }

    private void assertInvalidReturn()
    {
        inRequest( () -> assertThatThrownBy( this::execute ).isInstanceOf( IllegalArgumentException.class )
            .hasMessage( "MCP controller " + CONTROLLER + " must return an object" ) );
    }

    private AtomicReference<PortalRequest> captureTemporaryRequest()
    {
        final var temporary = new AtomicReference<PortalRequest>();
        when( scripts.hasScript( CONTROLLER ) ).thenAnswer( invocation -> {
            assertToolRequest();
            temporary.set( PortalRequestAccessor.get() );
            return true;
        } );
        return temporary;
    }

    private static void assertToolRequest()
    {
        final var request = PortalRequestAccessor.get();
        assertThat( request ).isNotNull();
        assertThat( request.getApplicationKey() ).isEqualTo( KEY.getApplicationKey() );
    }

    private static void inRequest( final Runnable work )
    {
        final var outer = new PortalRequest();
        outer.setApplicationKey( ApplicationKey.from( "outer.app" ) );
        final var request = new PortalRequest();
        request.setApplicationKey( CALLER );
        assertThat( PortalRequestAccessor.get() ).isNull();
        PortalRequestAccessor.runWith( outer, () -> {
            PortalRequestAccessor.runWith( request, () -> {
                try
                {
                    work.run();
                }
                finally
                {
                    assertThat( PortalRequestAccessor.get() ).isSameAs( request );
                    assertThat( request.getApplicationKey() ).isEqualTo( CALLER );
                }
            } );
            assertThat( PortalRequestAccessor.get() ).isSameAs( outer );
            assertThat( outer.getApplicationKey() ).isEqualTo( ApplicationKey.from( "outer.app" ) );
        } );
        assertThat( PortalRequestAccessor.get() ).isNull();
    }
}
