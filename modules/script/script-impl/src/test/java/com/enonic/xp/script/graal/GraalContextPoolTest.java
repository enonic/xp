package com.enonic.xp.script.graal;

import java.io.Closeable;
import java.net.URL;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.graal.executor.GraalContextBudget;
import com.enonic.xp.script.graal.executor.GraalScriptExecutor;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.impl.function.ApplicationInfoBuilder;
import com.enonic.xp.script.impl.service.ServiceRegistryImpl;
import com.enonic.xp.script.runtime.ScriptSettings;
import com.enonic.xp.server.RunMode;
import com.enonic.xp.server.RunModeSupport;
import com.enonic.xp.util.Version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraalContextPoolTest
{
    private ScriptExecutor scriptExecutor;

    private ExecutorService threads;

    private BundleContext bundleContext;

    private ApplicationInfoBuilder application;

    private ResourceService resourceService;

    @BeforeEach
    void setUp()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "graaljs" );

        this.bundleContext = Mockito.mock( BundleContext.class );

        this.application = new ApplicationInfoBuilder( applicationKey, ConfigBuilder.create().build(), Version.emptyVersion );

        this.resourceService = Mockito.mock( ResourceService.class );
        Mockito.when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final ResourceKey resourceKey = invocation.getArgument( 0 );
            final URL resourceUrl = GraalContextPoolTest.class.getResource( "/" + resourceKey.getApplicationKey() + resourceKey.getPath() );
            return new UrlResource( resourceKey, resourceUrl );
        } );

        this.scriptExecutor = newExecutor( 2, GraalContextBudget.unlimited() );
        this.threads = Executors.newFixedThreadPool( 2 );
    }

    private ScriptExecutor newExecutor( final int capacity, final GraalContextBudget budget )
    {
        return new GraalScriptExecutor( new GraalJSContextFactory(), getClass().getClassLoader(),
                                        ScriptSettings.create().build(), new ServiceRegistryImpl( bundleContext ), resourceService,
                                        application, capacity, budget );
    }

    @AfterEach
    void destroy()
        throws Exception
    {
        this.threads.shutdownNow();
        ( (Closeable) this.scriptExecutor ).close();
    }

    @Test
    @Timeout(60)
    void requestsExecuteInParallelOnSeparateSlots()
        throws Exception
    {
        final ScriptExports exports = scriptExecutor.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );

        // both invocations must be inside JS simultaneously to pass the barrier — impossible
        // with a single context
        final SyncPoint sync = new SyncPoint();
        final Future<ScriptValue> first = threads.submit( () -> exports.executeMethod( "block", sync ) );
        final Future<ScriptValue> second = threads.submit( () -> exports.executeMethod( "block", sync ) );

        // module state is per slot: each slot sees its own counter incremented once
        assertEquals( 1, intValue( first.get() ) );
        assertEquals( 1, intValue( second.get() ) );

        // a subsequent call lands on either slot — both are at 1, so the result is deterministic
        assertEquals( 2, intValue( exports.executeMethod( "inc" ) ) );
    }

    @Test
    @Timeout(60)
    void callbackRequiresInItsOwnSlot()
        throws Exception
    {
        final ScriptExports exports = scriptExecutor.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );

        final ScriptValue callback = exports.executeMethod( "mkCallback" );

        // a callback invoked on a foreign thread holds its context monitor; the require() inside
        // must resolve to the callback's own slot, not check out a different one
        final ScriptValue result = threads.submit( () -> callback.call() ).get();
        assertEquals( 42, ( (Number) result.getValue() ).intValue() );
    }

    @Test
    @Timeout(60)
    void boundViewSticksToCapturingSlot()
    {
        final ScriptExports exports = scriptExecutor.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );

        final ScriptExports bound = exports.executeBound( view -> {
            // inside the scope every execution shares the captured slot, through either handle
            assertEquals( 1, intValue( view.executeMethod( "inc" ) ) );
            assertEquals( 2, intValue( exports.executeMethod( "inc" ) ) );
            return view;
        } );

        // the view captured by the scope keeps executing on the exact same slot afterwards
        assertEquals( 3, intValue( bound.executeMethod( "inc" ) ) );
        assertEquals( 4, intValue( bound.executeMethod( "inc" ) ) );
    }

    @Test
    @Timeout(60)
    void retainedSlotIsSkippedByTheRequestPool()
    {
        final ScriptExports exports = scriptExecutor.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );

        // a connection captures the slot that served its request and retains it
        final ScriptExports connection = exports.executeBound( view -> {
            assertEquals( 1, intValue( view.executeMethod( "inc" ) ) );
            return view;
        } );
        connection.retain();
        try
        {
            // anonymous executions never land on the retained slot — its module state is
            // untouched no matter how many requests run
            for ( int i = 0; i < 8; i++ )
            {
                exports.executeMethod( "inc" );
            }
            assertEquals( 2, intValue( connection.executeMethod( "inc" ) ) );
        }
        finally
        {
            connection.release();
        }

        // released: the slot serves the request pool again — with both slots free, two
        // consecutive round-robin executions hit both, advancing the released slot's counter
        exports.executeMethod( "inc" );
        exports.executeMethod( "inc" );
        assertEquals( 4, intValue( connection.executeMethod( "inc" ) ) );
    }

    @Test
    @Timeout(60)
    void connectionEventsSerializeWithForeignThreadCallbacks()
        throws Exception
    {
        final ScriptExports exports = scriptExecutor.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );

        // a connection pinned to the exact slot that served its request (the websocket model)
        final ScriptExports connection = exports.executeBound( view -> view );
        connection.retain();
        try
        {
            // a callback created in the connection's context escapes to a foreign thread and
            // occupies the context (it takes the context monitor, not the slot lock)
            final Gate gate = new Gate();
            final ScriptValue blocker = connection.executeMethod( "mkBlocker", gate );
            final Future<ScriptValue> busy = threads.submit( () -> blocker.call() );
            assertTrue( gate.awaitEntered() );

            final Future<?> opener = threads.submit( () -> {
                Thread.sleep( 300 );
                gate.open();
                return null;
            } );

            // an event for the connection must wait for the callback's turn to end: it observes
            // the callback's increment, proving mutual exclusion on the shared context
            assertEquals( 2, intValue( connection.executeMethod( "inc" ) ) );

            assertEquals( 1, intValue( busy.get() ) );
            opener.get();
        }
        finally
        {
            connection.release();
        }
    }

    @Test
    @Timeout(60)
    void allSlotsRetainedStarvesRequests()
        throws Exception
    {
        final ScriptExecutor limited = newExecutor( 1, GraalContextBudget.unlimited() );
        try
        {
            final ScriptExports exports = limited.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );

            final ScriptExports connection = exports.executeBound( view -> {
                assertEquals( 1, intValue( view.executeMethod( "inc" ) ) );
                return view;
            } );
            connection.retain();
            try
            {
                // exclusivity over liveness: the retained context carries the connection's
                // module state, and GraalJS offers no safe sharing — the request fails loudly,
                // and the message names the failure mode for operators
                final IllegalStateException e =
                    assertThrows( IllegalStateException.class, () -> exports.executeMethod( "inc" ) );
                assertTrue( e.getMessage().contains( "retained" ), e.getMessage() );
                assertTrue( e.getMessage().contains( "at capacity" ), e.getMessage() );
            }
            finally
            {
                connection.release();
            }

            // released: the slot serves requests again
            assertEquals( 2, intValue( exports.executeMethod( "inc" ) ) );
        }
        finally
        {
            ( (Closeable) limited ).close();
        }
    }

    @Test
    @Timeout(60)
    void requiresJsonInSlot()
        throws Exception
    {
        final ScriptExports exports = scriptExecutor.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );

        assertEquals( 7, intValue( exports.executeMethod( "readJson" ) ) );
    }

    @Test
    @Timeout(60)
    void exportsResolveAcrossPool()
        throws Exception
    {
        final ScriptExports exports = scriptExecutor.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );

        assertTrue( exports.hasMethod( "inc" ) );
        assertFalse( exports.hasMethod( "unknown" ) );
        assertNull( exports.executeMethod( "unknown" ) );
        assertNotNull( exports.getValue() );
        assertNotNull( exports.getRawValue() );
        assertNotNull( scriptExecutor.newScriptValue( "scalar" ) );
    }

    @Test
    @Timeout(60)
    void executeMethodSkipsThePool()
    {
        // executeMethod touches no pooled slot, and every call gets a fresh, private context —
        // the path named tasks use, so they never compete with request traffic. A fresh context
        // per call: the module counter starts over every time, and the scalar result survives
        // the private context's close
        assertEquals( 1, ( (Number) scriptExecutor.executeMethod( ResourceKey.from( "graaljs:pool-test.js" ), "inc" ) ).intValue() );
        assertEquals( 1, ( (Number) scriptExecutor.executeMethod( ResourceKey.from( "graaljs:pool-test.js" ), "inc" ) ).intValue() );
        // a non-scalar result cannot survive the private context and comes back as null
        assertNull( scriptExecutor.executeMethod( ResourceKey.from( "graaljs:pool-test.js" ), "mkCallback" ) );

        // the pooled contexts are untouched by isolated runs
        final ScriptExports exports = scriptExecutor.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );
        assertEquals( 1, intValue( exports.executeMethod( "inc" ) ) );
    }

    @Test
    @Timeout(60)
    void executeMethodRequiresTheMethod()
    {
        // a missing method must fail loudly - executeMethod's null is a legal scalar-contract result
        assertThrows( IllegalArgumentException.class,
                      () -> scriptExecutor.executeMethod( ResourceKey.from( "graaljs:pool-test.js" ), "noSuchMethod" ) );
    }

    @Test
    @Timeout(60)
    void closedExecutorRejectsNewContexts()
        throws Exception
    {
        final ScriptExecutor local = newExecutor( 1, GraalContextBudget.unlimited() );
        ( (Closeable) local ).close();

        // a bootstrap racing application stop must fail loudly, not resurrect contexts that no
        // teardown path can ever reach
        assertThrows( IllegalStateException.class, () -> local.bootstrap( ResourceKey.from( "graaljs:/main.js" ) ) );
        assertThrows( IllegalStateException.class, () -> local.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) ) );
        assertThrows( IllegalStateException.class, () -> local.executeMethod( ResourceKey.from( "graaljs:pool-test.js" ), "inc" ) );
    }

    @Test
    @Timeout(60)
    void closedExecutorRejectsExecutionOnExistingSlots()
        throws Exception
    {
        final ScriptExecutor local = newExecutor( 1, GraalContextBudget.unlimited() );
        // populate the pool before closing: close() cancels the contexts but keeps the slot
        // entries, so post-close execution must be rejected up front, not fail deep inside a
        // cancelled context
        local.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );
        ( (Closeable) local ).close();

        final IllegalStateException e = assertThrows( IllegalStateException.class,
                                                      () -> local.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) ) );
        assertEquals( "Script executor is closed", e.getMessage() );
    }

    @Test
    @Timeout(60)
    void retainAndBindAreNoOpsWithoutAPinnedSlot()
    {
        final ScriptExports exports = scriptExecutor.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );

        // views without a pinned slot have nothing to retain — both calls are safe no-ops
        exports.retain();
        exports.release();
    }

    @Test
    @Timeout(60)
    void budgetExhaustionFallsBackToExistingSlot()
        throws Exception
    {
        final ScriptExecutor limited = newExecutor( 4, new GraalContextBudget( 0, 4, 4 ) );
        try
        {
            final ScriptExports exports = limited.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );

            // only the first (unbudgeted) slot exists; every bound capture resolves to it
            final ScriptExports first = exports.executeBound( view -> view );
            final ScriptExports second = exports.executeBound( view -> view );
            assertEquals( 1, intValue( first.executeMethod( "inc" ) ) );
            assertEquals( 2, intValue( second.executeMethod( "inc" ) ) );
        }
        finally
        {
            ( (Closeable) limited ).close();
        }
    }

    @Test
    @Timeout(60)
    void connectionBudgetRejectsTheMarginalConnection()
        throws Exception
    {
        final ScriptExecutor limited = newExecutor( 2, new GraalContextBudget( Integer.MAX_VALUE, 1, 1 ) );
        try
        {
            final ScriptExports exports = limited.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );

            final ScriptExports first = exports.executeBound( view -> view );
            first.retain();
            try
            {
                // one connection is the budget: the next open fails — and it is the connection
                // that is rejected, the request pool keeps serving
                final ScriptExports second = exports.executeBound( view -> view );
                final IllegalStateException e = assertThrows( IllegalStateException.class, second::retain );
                assertTrue( e.getMessage().contains( "connection budget" ), e.getMessage() );
                assertEquals( 1, intValue( exports.executeMethod( "inc" ) ) );
            }
            finally
            {
                first.release();
            }

            // released: the permit is back, and an unpaired release must not mint another
            final ScriptExports third = exports.executeBound( view -> view );
            third.retain();
            third.release();
            third.release();
            final ScriptExports fourth = exports.executeBound( view -> view );
            fourth.retain();
            try
            {
                assertThrows( IllegalStateException.class, () -> exports.executeBound( view -> view ).retain() );
            }
            finally
            {
                fourth.release();
            }
        }
        finally
        {
            ( (Closeable) limited ).close();
        }
    }

    @Test
    @Timeout(60)
    void devModeCompilesSourcesFreshAndKeepsServing()
    {
        RunModeSupport.set( RunMode.DEV );
        try
        {
            // dev mode skips the strong source registry (fresh compile per require) and checks
            // cache expiry on every top-level execution — unchanged resources keep their module
            // state across executions on the same slot
            final ScriptExports exports = scriptExecutor.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );
            assertEquals( 1, intValue( exports.executeMethod( "inc" ) ) );
            scriptExecutor.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );
            assertEquals( 2, intValue( exports.executeMethod( "inc" ) ) );
        }
        finally
        {
            RunModeSupport.set( RunMode.PROD );
        }
    }

    @Test
    @Timeout(60)
    void executorCloseReturnsTheMainContextsConnectionPermits()
        throws Exception
    {
        final GraalContextBudget budget = new GraalContextBudget( Integer.MAX_VALUE, 1, 1 );
        final ScriptExecutor first = newExecutor( 1, budget );
        try
        {
            // bootstrap exports are pinned to the main context, which lives outside the slot array
            first.bootstrap( ResourceKey.from( "graaljs:main.js" ) ).retain();
        }
        finally
        {
            ( (Closeable) first ).close();
        }

        // the main context's pin is drained by teardown too, so its permit is back in the budget
        final ScriptExecutor second = newExecutor( 1, budget );
        try
        {
            second.bootstrap( ResourceKey.from( "graaljs:main.js" ) ).retain();
        }
        finally
        {
            ( (Closeable) second ).close();
        }
    }

    @Test
    @Timeout(60)
    void executorCloseReturnsOrphanedConnectionPermits()
        throws Exception
    {
        final GraalContextBudget budget = new GraalContextBudget( Integer.MAX_VALUE, 1, 1 );
        final ScriptExecutor first = newExecutor( 1, budget );
        final ScriptExports orphan;
        try
        {
            final ScriptExports exports = first.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );
            orphan = exports.executeBound( view -> view );
            // the connection's terminal event never arrives: no release before the app dies
            orphan.retain();
        }
        finally
        {
            ( (Closeable) first ).close();
        }

        // teardown drained the orphaned pin and returned its permit to the shared budget
        final ScriptExecutor second = newExecutor( 2, budget );
        try
        {
            final ScriptExports exports = second.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );
            final ScriptExports connection = exports.executeBound( view -> view );
            connection.retain();
            try
            {
                // a late release from the orphaned endpoint finds no pin and returns no permit
                orphan.release();
                final IllegalStateException e =
                    assertThrows( IllegalStateException.class, () -> exports.executeBound( view -> view ).retain() );
                assertTrue( e.getMessage().contains( "connection budget" ), e.getMessage() );
            }
            finally
            {
                connection.release();
            }
        }
        finally
        {
            ( (Closeable) second ).close();
        }
    }

    @Test
    @Timeout(60)
    void failedRetainDoesNotStealAnotherConnectionsPin()
        throws Exception
    {
        final ScriptExecutor limited = newExecutor( 1, new GraalContextBudget( Integer.MAX_VALUE, 1, 1 ) );
        try
        {
            final ScriptExports exports = limited.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );

            // both views capture the single slot before any retention
            final ScriptExports a = exports.executeBound( view -> view );
            final ScriptExports b = exports.executeBound( view -> view );
            a.retain();
            try
            {
                assertThrows( IllegalStateException.class, b::retain );
                // the endpoint releases after a failed open: it must not strip a's pin
                b.release();
                assertThrows( IllegalStateException.class, () -> exports.executeMethod( "inc" ) );
            }
            finally
            {
                a.release();
            }
            assertEquals( 1, intValue( exports.executeMethod( "inc" ) ) );
        }
        finally
        {
            ( (Closeable) limited ).close();
        }
    }

    @Test
    @Timeout(60)
    void waitsFairlyWhenAtCapacity()
        throws Exception
    {
        final ScriptExecutor limited = newExecutor( 1, GraalContextBudget.unlimited() );
        try
        {
            final ScriptExports exports = limited.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );

            final Gate gate = new Gate();
            final Future<ScriptValue> blocked = threads.submit( () -> exports.executeMethod( "block", gate ) );
            assertTrue( gate.awaitEntered() );

            final Future<?> opener = threads.submit( () -> {
                Thread.sleep( 300 );
                gate.open();
                return null;
            } );

            // capacity 1 and the only slot is busy inside `block`: this execution must wait for it
            assertEquals( 2, intValue( exports.executeMethod( "inc" ) ) );

            assertEquals( 1, intValue( blocked.get() ) );
            opener.get();
        }
        finally
        {
            ( (Closeable) limited ).close();
        }
    }

    @Test
    @Timeout(60)
    void executesOnVirtualThreads()
        throws Exception
    {
        final ScriptExports exports = scriptExecutor.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );

        try (ExecutorService virtualThreads = Executors.newVirtualThreadPerTaskExecutor())
        {
            assertEquals( 1, virtualThreads.submit( () -> intValue( exports.executeMethod( "inc" ) ) ).get() );
            assertEquals( 1, ( (Number) virtualThreads.submit(
                () -> scriptExecutor.executeMethod( ResourceKey.from( "graaljs:pool-test.js" ), "inc" ) ).get() ).intValue() );
        }
    }

    @Test
    @Timeout(60)
    void mainJsGetsADedicatedContext()
        throws Exception
    {
        final ScriptExports main = scriptExecutor.bootstrap( ResourceKey.from( "graaljs:/main.js" ) );
        assertEquals( 1, intValue( main.executeMethod( "inc" ) ) );

        // request traffic executes on pool slots and never touches the main context
        final ScriptExports exports = scriptExecutor.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );
        for ( int i = 0; i < 8; i++ )
        {
            exports.executeMethod( "inc" );
        }
        assertEquals( 2, intValue( main.executeMethod( "inc" ) ) );

        // a listener created by main.js executes in the main context from any thread,
        // sharing main.js module state
        final ScriptValue listener = main.executeMethod( "mkListener" );
        final ScriptValue result = threads.submit( () -> listener.call() ).get();
        assertEquals( 3, ( (Number) result.getValue() ).intValue() );
    }

    @Test
    @Timeout(60)
    void closeRunsPendingDisposersOnce()
        throws Exception
    {
        final ScriptExecutor local = newExecutor( 1, GraalContextBudget.unlimited() );
        final AtomicInteger runs = new AtomicInteger();
        local.registerMock( "/test/recorder", runs );

        // __.disposer is honored from the main context (bootstrap)
        local.bootstrap( ResourceKey.from( "graaljs:/disposer-test.js" ) );
        assertEquals( 0, runs.get() );

        // instance-owned teardown: closing the executor runs its disposers, exactly once
        ( (Closeable) local ).close();
        assertEquals( 1, runs.get() );

        ( (Closeable) local ).close();
        assertEquals( 1, runs.get() );
    }

    @Test
    @Timeout(60)
    void throwingDisposerDoesNotStopTeardown()
        throws Exception
    {
        final ScriptExecutor local = newExecutor( 1, GraalContextBudget.unlimited() );
        final AtomicInteger runs = new AtomicInteger();
        local.registerMock( "/test/recorder", runs );

        local.bootstrap( ResourceKey.from( "graaljs:/disposer-throwing-test.js" ) );

        // teardown is best-effort: the first disposer throws, the second still runs, close completes
        ( (Closeable) local ).close();
        assertEquals( 1, runs.get() );
    }

    @Test
    @Timeout(60)
    void disposerOutsideMainContextIsIgnored()
        throws Exception
    {
        final ScriptExecutor local = newExecutor( 1, GraalContextBudget.unlimited() );
        final AtomicInteger runs = new AtomicInteger();
        local.registerMock( "/test/recorder", runs );

        // executeMain runs on a pool slot, not the main context: __.disposer is ignored
        local.executeMain( ResourceKey.from( "graaljs:/disposer-test.js" ) );
        ( (Closeable) local ).close();

        assertEquals( 0, runs.get() );
    }

    private static int intValue( final ScriptValue value )
    {
        return ( (Number) value.getValue() ).intValue();
    }

    public static class Gate
    {
        private final CountDownLatch entered = new CountDownLatch( 1 );

        private final CountDownLatch release = new CountDownLatch( 1 );

        public void await()
            throws InterruptedException
        {
            entered.countDown();
            release.await();
        }

        void open()
        {
            release.countDown();
        }

        boolean awaitEntered()
            throws InterruptedException
        {
            return entered.await( 10, TimeUnit.SECONDS );
        }
    }

    public static class SyncPoint
    {
        private final CyclicBarrier barrier = new CyclicBarrier( 2 );

        public void await()
            throws InterruptedException, BrokenBarrierException, TimeoutException
        {
            barrier.await( 30, TimeUnit.SECONDS );
        }
    }
}
