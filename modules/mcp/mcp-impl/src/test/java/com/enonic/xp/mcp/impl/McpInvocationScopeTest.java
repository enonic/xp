package com.enonic.xp.mcp.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.mcp.McpInvocation;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.security.auth.AuthenticationInfo;

import io.modelcontextprotocol.modern.McpSchema.CallToolOutcome;
import io.modelcontextprotocol.modern.McpSchema.CallToolResult;
import io.modelcontextprotocol.modern.server.McpRequestContext;
import io.modelcontextprotocol.modern.server.McpSyncNotifier;
import io.modelcontextprotocol.modern.server.McpSyncResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@NullMarked
class McpInvocationScopeTest
{
    private static final CallToolResult RESULT = CallToolResult.builder().build();

    private final McpSyncNotifier notifier = mock( McpSyncNotifier.class );

    @Test
    void overloads_emit_only_increasing_progress_and_omit_optional_fields()
    {
        final var response = streaming( () -> {
            final var invocation = McpInvocationScope.current();
            assertThat( invocation.isProgressSupported() ).isTrue();
            invocation.progress( 0 );
            invocation.progress( 0, "repeated" );
            invocation.progress( 1, "working" );
            invocation.progress( 0.5 );
            invocation.progress( 2, 4 );
            invocation.progress( 3, 4, "almost" );
            invocation.progress( 4, 4, "done" );
            return RESULT;
        } );
        verifyNoInteractions( notifier );
        assertThat( response.body().run( notifier ) ).isSameAs( RESULT );
        final var order = inOrder( notifier );
        order.verify( notifier ).progress( 0, null, null );
        order.verify( notifier ).progress( 1, null, "working" );
        order.verify( notifier ).progress( 2, 4.0, null );
        order.verify( notifier ).progress( 3, 4.0, "almost" );
        order.verify( notifier ).progress( 4, 4.0, "done" );
        verifyNoMoreInteractions( notifier );
    }

    @Test
    void validates_values_without_advancing_progress()
    {
        streaming( () -> {
            final var invocation = McpInvocationScope.current();
            for ( final var invalid : List.of( -1.0, Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY ) )
            {
                assertThatThrownBy( () -> invocation.progress( invalid ) ).isInstanceOf( IllegalArgumentException.class );
                assertThatThrownBy( () -> invocation.progress( invalid, "message" ) ).isInstanceOf( IllegalArgumentException.class );
                assertThatThrownBy( () -> invocation.progress( 0, invalid ) ).isInstanceOf( IllegalArgumentException.class );
                assertThatThrownBy( () -> invocation.progress( 0, invalid, "message" ) ).isInstanceOf( IllegalArgumentException.class );
                assertThatThrownBy( () -> invocation.progress( invalid, 10 ) ).isInstanceOf( IllegalArgumentException.class );
                assertThatThrownBy( () -> invocation.progress( invalid, 10, "message" ) ).isInstanceOf( IllegalArgumentException.class );
            }
            assertThatThrownBy( () -> invocation.progress( 2, 1 ) ).isInstanceOf( IllegalArgumentException.class );
            assertThatThrownBy( () -> invocation.progress( 2, 1, "message" ) ).isInstanceOf( IllegalArgumentException.class );
            invocation.progress( 0, 0 );
            invocation.progress( 1, 1 );
            return RESULT;
        } ).body().run( notifier );
        verify( notifier ).progress( 0, 0.0, null );
        verify( notifier ).progress( 1, 1.0, null );
        verifyNoMoreInteractions( notifier );
    }

    @Test
    void rejects_invalid_progress_tokens_before_invocation()
    {
        for ( final var token : List.of( 1.5, Double.NaN, Double.POSITIVE_INFINITY, true, List.of( "token" ),
                                         Map.of( "token", "value" ) ) )
        {
            final var context = mock( McpRequestContext.class );
            when( context.progressToken() ).thenReturn( token );
            final var calls = new AtomicInteger();
            assertThatThrownBy( () -> McpInvocationScope.respond( context, () -> {
                calls.incrementAndGet();
                return RESULT;
            } ) ).hasMessageContaining( "progressToken must be a string or integer" );
            assertThat( calls ).hasValue( 0 );
        }
        verifyNoInteractions( notifier );
    }

    @Test
    void no_token_runs_once_synchronously_with_inactive_helper_and_json_result()
    {
        final var calls = new AtomicInteger();
        final var thread = Thread.currentThread();
        final var response = McpInvocationScope.respond( mock( McpRequestContext.class ), () -> {
            calls.incrementAndGet();
            assertThat( Thread.currentThread() ).isSameAs( thread );
            assertInactive( McpInvocationScope.current() );
            return RESULT;
        } );
        assertThat( response ).isInstanceOf( McpSyncResponse.Result.class );
        assertThat( ( (McpSyncResponse.Result<CallToolOutcome>) response ).result() ).isSameAs( RESULT );
        assertThat( calls ).hasValue( 1 );
        assertInactive( McpInvocationScope.current() );
        verifyNoInteractions( notifier );
    }

    @Test
    void no_token_failure_runs_once_and_restores_outer_helper()
    {
        final var calls = new AtomicInteger();
        final var failure = new IllegalStateException( "no-token" );
        streaming( () -> {
            final var outer = McpInvocationScope.current();
            assertThatThrownBy( () -> McpInvocationScope.respond( mock( McpRequestContext.class ), () -> {
                calls.incrementAndGet();
                assertInactive( McpInvocationScope.current() );
                throw failure;
            } ) ).isSameAs( failure );
            assertThat( McpInvocationScope.current() ).isSameAs( outer );
            assertThat( outer.isProgressSupported() ).isTrue();
            return RESULT;
        } ).body().run( notifier );
        assertThat( calls ).hasValue( 1 );
        assertInactive( McpInvocationScope.current() );
        verifyNoInteractions( notifier );
    }

    @Test
    void completion_and_failure_deactivate_retained_helpers()
    {
        final var retained = new AtomicReference<McpInvocation>();
        streaming( () -> {
            retained.set( McpInvocationScope.current() );
            retained.get().progress( 1 );
            return RESULT;
        } ).body().run( notifier );
        assertInactive( retained.get() );
        final var failure = new IllegalStateException( "failed" );
        final var response = streaming( () -> {
            retained.set( McpInvocationScope.current() );
            throw failure;
        } );
        assertThatThrownBy( () -> response.body().run( notifier ) ).isSameAs( failure );
        assertInactive( retained.get() );
        assertInactive( McpInvocationScope.current() );
        verify( notifier ).progress( 1, null, null );
        verifyNoMoreInteractions( notifier );
    }

    @Test
    void foreign_thread_cannot_use_active_helper()
    {
        streaming( () -> {
            final var invocation = McpInvocationScope.current();
            try ( var workers = Executors.newSingleThreadExecutor() )
            {
                assertDoesNotThrow( () -> workers.submit( () -> {
                    assertInactive( invocation );
                    assertInactive( McpInvocationScope.current() );
                } ).get( 10, TimeUnit.SECONDS ) );
            }
            assertThat( invocation.isProgressSupported() ).isTrue();
            invocation.progress( 1 );
            return RESULT;
        } ).body().run( notifier );
        verify( notifier ).progress( 1, null, null );
        verifyNoMoreInteractions( notifier );
    }

    @Test
    void nested_scopes_restore_outer_helper_on_success_and_error()
    {
        final var innerNotifier = mock( McpSyncNotifier.class );
        streaming( () -> {
            final var outer = McpInvocationScope.current();
            outer.progress( 1 );
            streaming( () -> {
                assertInactive( outer );
                assertThat( McpInvocationScope.current() ).isNotSameAs( outer );
                McpInvocationScope.current().progress( 1 );
                return RESULT;
            } ).body().run( innerNotifier );
            McpInvocationScope.respond( mock( McpRequestContext.class ), () -> {
                assertInactive( outer );
                assertInactive( McpInvocationScope.current() );
                return RESULT;
            } );
            final var failed = streaming( () -> {
                throw new IllegalStateException( "nested" );
            } );
            assertThatThrownBy( () -> failed.body().run( innerNotifier ) ).hasMessage( "nested" );
            assertThat( McpInvocationScope.current() ).isSameAs( outer );
            assertThat( outer.isProgressSupported() ).isTrue();
            outer.progress( 2 );
            return RESULT;
        } ).body().run( notifier );
        verify( notifier ).progress( 1, null, null );
        verify( notifier ).progress( 2, null, null );
        verify( innerNotifier ).progress( 1, null, null );
        verifyNoMoreInteractions( notifier, innerNotifier );
    }

    @Test
    void deferred_worker_preserves_caller_context_and_independent_portal_state_and_restores_worker()
    {
        final var auth = mock( AuthenticationInfo.class );
        final var caller = ContextBuilder.create().authInfo( auth ).repositoryId( "caller" ).branch( "draft" )
            .attribute( "attribute", "caller-value" ).build();
        caller.getLocalScope().setAttribute( "local", "captured" );
        final var portal = new PortalRequest();
        final var application = ApplicationKey.from( "caller.app" );
        portal.setApplicationKey( application );
        portal.setBaseUri( "/caller" );
        final var response = caller.callWith( () -> PortalRequestAccessor.callWith( portal, () -> streaming( () -> {
            final var current = ContextAccessor.current();
            assertThat( current ).isNotSameAs( caller );
            assertThat( current.getAuthInfo() ).isSameAs( auth );
            assertThat( current.getRepositoryId() ).isEqualTo( caller.getRepositoryId() );
            assertThat( current.getBranch() ).isEqualTo( caller.getBranch() );
            assertThat( current.getAttribute( "attribute" ) ).isEqualTo( "caller-value" );
            assertThat( current.getAttribute( "local" ) ).isEqualTo( "captured" );
            final var request = PortalRequestAccessor.get();
            assertThat( request ).isNotNull().isNotSameAs( portal );
            assertThat( request.getApplicationKey() ).isEqualTo( application );
            assertThat( request.getBaseUri() ).isEqualTo( "/caller" );
            request.setApplicationKey( ApplicationKey.from( "tool.app" ) );
            current.getLocalScope().setAttribute( "local", "worker-mutation" );
            McpInvocationScope.current().progress( 1 );
            return RESULT;
        } ) ) );
        portal.setApplicationKey( ApplicationKey.from( "servlet.app" ) );
        caller.getLocalScope().setAttribute( "local", "servlet-mutation" );
        final var worker = ContextBuilder.create().repositoryId( "worker" ).branch( "master" ).build();
        final var workerPortal = new PortalRequest();
        try ( var workers = Executors.newSingleThreadExecutor() )
        {
            assertDoesNotThrow( () -> workers.submit( () -> worker.runWith( () -> PortalRequestAccessor.runWith( workerPortal, () -> {
                assertThat( response.body().run( notifier ) ).isSameAs( RESULT );
                assertThat( ContextAccessor.current() ).isSameAs( worker );
                assertThat( PortalRequestAccessor.get() ).isSameAs( workerPortal );
                assertInactive( McpInvocationScope.current() );
            } ) ) ).get( 10, TimeUnit.SECONDS ) );
        }
        assertThat( portal.getApplicationKey() ).isEqualTo( ApplicationKey.from( "servlet.app" ) );
        assertThat( caller.getAttribute( "local" ) ).isEqualTo( "servlet-mutation" );
        verify( notifier ).progress( 1, null, null );
    }

    @Test
    void missing_caller_portal_masks_worker_portal_with_independent_request()
    {
        assertThat( PortalRequestAccessor.get() ).isNull();
        final var response = streaming( () -> {
            final var request = PortalRequestAccessor.get();
            assertThat( request ).isNotNull();
            assertThat( request.getApplicationKey() ).isNull();
            request.setApplicationKey( ApplicationKey.from( "tool.app" ) );
            return RESULT;
        } );
        final var workerPortal = new PortalRequest();
        workerPortal.setApplicationKey( ApplicationKey.from( "worker.app" ) );
        PortalRequestAccessor.runWith( workerPortal, () -> {
            assertThat( response.body().run( notifier ) ).isSameAs( RESULT );
            assertThat( PortalRequestAccessor.get() ).isSameAs( workerPortal );
            assertThat( workerPortal.getApplicationKey() ).isEqualTo( ApplicationKey.from( "worker.app" ) );
        } );
        assertThat( PortalRequestAccessor.get() ).isNull();
    }

    @Test
    void error_restores_worker_context_and_portal()
    {
        final var response = streaming( () -> {
            throw new IllegalArgumentException( "controller" );
        } );
        final var worker = ContextBuilder.create().repositoryId( "worker" ).build();
        final var portal = new PortalRequest();
        worker.runWith( () -> PortalRequestAccessor.runWith( portal, () -> {
            assertThatThrownBy( () -> response.body().run( notifier ) ).hasMessage( "controller" );
            assertThat( ContextAccessor.current() ).isSameAs( worker );
            assertThat( PortalRequestAccessor.get() ).isSameAs( portal );
            assertInactive( McpInvocationScope.current() );
        } ) );
    }

    @Test
    void concurrent_invocations_keep_contexts_helpers_and_notifiers_separate()
    {
        final var barrier = new CyclicBarrier( 2 );
        final var otherNotifier = mock( McpSyncNotifier.class );
        final var first = ContextBuilder.create().repositoryId( "first" ).build().callWith( () -> streaming( () -> {
            await( barrier );
            assertThat( ContextAccessor.current().getRepositoryId().toString() ).isEqualTo( "first" );
            McpInvocationScope.current().progress( 1, "first" );
            return RESULT;
        } ) );
        final var second = ContextBuilder.create().repositoryId( "second" ).build().callWith( () -> streaming( () -> {
            await( barrier );
            assertThat( ContextAccessor.current().getRepositoryId().toString() ).isEqualTo( "second" );
            McpInvocationScope.current().progress( 1, "second" );
            return RESULT;
        } ) );
        try ( var workers = Executors.newFixedThreadPool( 2 ) )
        {
            final var firstResult = workers.submit( () -> first.body().run( notifier ) );
            final var secondResult = workers.submit( () -> second.body().run( otherNotifier ) );
            assertThat( assertDoesNotThrow( () -> firstResult.get( 10, TimeUnit.SECONDS ) ) ).isSameAs( RESULT );
            assertThat( assertDoesNotThrow( () -> secondResult.get( 10, TimeUnit.SECONDS ) ) ).isSameAs( RESULT );
        }
        verify( notifier ).progress( 1, null, "first" );
        verify( otherNotifier ).progress( 1, null, "second" );
        verifyNoMoreInteractions( notifier, otherNotifier );
    }

    private static McpSyncResponse.Streaming<CallToolOutcome> streaming( final Supplier<CallToolResult> action )
    {
        final var context = mock( McpRequestContext.class );
        when( context.progressToken() ).thenReturn( "client-token" );
        return (McpSyncResponse.Streaming<CallToolOutcome>) McpInvocationScope.respond( context, action );
    }

    private static void assertInactive( final McpInvocation invocation )
    {
        assertThat( invocation.isProgressSupported() ).isFalse();
        assertDoesNotThrow( () -> {
            invocation.progress( Double.NaN );
            invocation.progress( -1, "ignored" );
            invocation.progress( 2, 1 );
            invocation.progress( 2, 1, "ignored" );
        } );
    }

    private static void await( final CyclicBarrier barrier )
    {
        assertDoesNotThrow( () -> barrier.await( 10, TimeUnit.SECONDS ) );
    }
}
