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
import com.enonic.xp.util.Version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        return new GraalScriptExecutor( new GraalJSContextFactory(), Executors.newSingleThreadExecutor(), getClass().getClassLoader(),
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
    void pinnedExportsStickToOneSlot()
    {
        final ScriptExports exports = scriptExecutor.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );

        final ScriptExports pinned = exports.pinned( "connection-1" );
        assertEquals( 1, intValue( pinned.executeMethod( "inc" ) ) );
        assertEquals( 2, intValue( pinned.executeMethod( "inc" ) ) );
        assertEquals( 3, intValue( pinned.executeMethod( "inc" ) ) );

        // an equal key resolves to the same slot on a fresh view
        assertEquals( 4, intValue( exports.pinned( "connection-1" ).executeMethod( "inc" ) ) );
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
        final ScriptExports exports =
            scriptExecutor.executeMainAsync( ResourceKey.from( "graaljs:pool-test.js" ) ).get( 30, TimeUnit.SECONDS );

        assertTrue( exports.hasMethod( "inc" ) );
        assertFalse( exports.hasMethod( "unknown" ) );
        assertNull( exports.executeMethod( "unknown" ) );
        assertNotNull( exports.getValue() );
        assertNotNull( exports.getRawValue() );
        assertNotNull( scriptExecutor.newScriptValue( "scalar" ) );
    }

    @Test
    @Timeout(60)
    void isolatedExecutionsGetFreshContexts()
    {
        final ScriptExports exports = scriptExecutor.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );

        // every isolated invocation runs in a fresh, private context
        assertEquals( 1, intValue( exports.isolated().executeMethod( "inc" ) ) );
        assertEquals( 1, intValue( exports.isolated().executeMethod( "inc" ) ) );

        // the pooled contexts are untouched by isolated runs
        assertEquals( 1, intValue( exports.executeMethod( "inc" ) ) );
    }

    @Test
    @Timeout(60)
    void budgetExhaustionFallsBackToExistingSlot()
        throws Exception
    {
        final ScriptExecutor limited = newExecutor( 4, new GraalContextBudget( 0, 4 ) );
        try
        {
            final ScriptExports exports = limited.executeMain( ResourceKey.from( "graaljs:pool-test.js" ) );

            // only the first (unbudgeted) slot exists; differently hashed keys share it
            assertEquals( 1, intValue( exports.pinned( "a" ).executeMethod( "inc" ) ) );
            assertEquals( 2, intValue( exports.pinned( "b" ).executeMethod( "inc" ) ) );
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
            assertEquals( 1, virtualThreads.submit( () -> intValue( exports.isolated().executeMethod( "inc" ) ) ).get() );
        }
    }

    @Test
    @Timeout(60)
    void disposersRunAtMostOnce()
    {
        final AtomicInteger runs = new AtomicInteger();
        scriptExecutor.registerDisposer( ResourceKey.from( "graaljs:pool-test.js" ), runs::incrementAndGet );

        scriptExecutor.runDisposers();
        scriptExecutor.runDisposers();

        assertEquals( 1, runs.get() );
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
