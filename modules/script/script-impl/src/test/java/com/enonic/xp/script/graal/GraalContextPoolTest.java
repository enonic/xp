package com.enonic.xp.script.graal;

import java.io.Closeable;
import java.net.URL;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    @BeforeEach
    void setUp()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "graaljs" );

        final BundleContext bundleContext = Mockito.mock( BundleContext.class );

        final ApplicationInfoBuilder application =
            new ApplicationInfoBuilder( applicationKey, ConfigBuilder.create().build(), Version.emptyVersion );

        final ResourceService resourceService = Mockito.mock( ResourceService.class );
        Mockito.when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final ResourceKey resourceKey = invocation.getArgument( 0 );
            final URL resourceUrl = GraalContextPoolTest.class.getResource( "/" + resourceKey.getApplicationKey() + resourceKey.getPath() );
            return new UrlResource( resourceKey, resourceUrl );
        } );

        this.scriptExecutor =
            new GraalScriptExecutor( new GraalJSContextFactory(), Executors.newSingleThreadExecutor(), getClass().getClassLoader(),
                                     ScriptSettings.create().build(), new ServiceRegistryImpl( bundleContext ), resourceService,
                                     application, 2 );
        this.threads = Executors.newFixedThreadPool( 2 );
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

    private static int intValue( final ScriptValue value )
    {
        return ( (Number) value.getValue() ).intValue();
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
