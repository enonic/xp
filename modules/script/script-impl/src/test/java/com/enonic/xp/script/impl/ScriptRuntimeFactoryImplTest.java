package com.enonic.xp.script.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.graalvm.polyglot.Engine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.impl.standard.ScriptRuntimeImpl;
import com.enonic.xp.script.runtime.ScriptSettings;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScriptRuntimeFactoryImplTest
{
    @Mock
    BundleContext bundleContext;

    @Mock(stubOnly = true)
    ResourceService resourceService;

    @BeforeEach
    void setup()
        throws Exception
    {
        // let the ServiceTracker construct and open against the mock registry (no initial services)
        lenient().when( this.bundleContext.createFilter( anyString() ) )
            .thenAnswer( invocation -> FrameworkUtil.createFilter( invocation.getArgument( 0 ) ) );
        lenient().when( this.bundleContext.getServiceReferences( anyString(), nullable( String.class ) ) ).thenReturn( null );
    }

    @Test
    @SuppressWarnings("unchecked")
    void removedService_invalidatesRuntimes()
    {
        final ScriptRuntimeFactoryImpl scriptRuntimeFactory =
            spy( new ScriptRuntimeFactoryImpl( bundleContext, resourceService ) );

        final ScriptRuntimeImpl scriptRuntime = mock( ScriptRuntimeImpl.class );
        when( scriptRuntimeFactory.doCreate( any() ) ).thenReturn( scriptRuntime );

        scriptRuntimeFactory.create( ScriptSettings.create().build() );

        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        final Application application = mock( Application.class );
        when( application.getKey() ).thenReturn( applicationKey );
        scriptRuntimeFactory.removedService( mock( ServiceReference.class ), application );

        // app stop is a full instance teardown, not a name-keyed disposer lookup (#10844)
        verify( scriptRuntime ).invalidate( eq( applicationKey ) );
    }

    @Test
    void sharedEngine_isCreatedOnceForEveryCaller()
        throws Exception
    {
        final ScriptRuntimeFactoryImpl scriptRuntimeFactory = new ScriptRuntimeFactoryImpl( bundleContext, resourceService );

        // every GraalJS application must be built on the same engine to share its code cache, and
        // each runtime resolves it through its own executor factory — concurrently on startup
        final int callers = 8;
        final CyclicBarrier barrier = new CyclicBarrier( callers );
        final ExecutorService threads = Executors.newFixedThreadPool( callers );
        try
        {
            final List<Future<Engine>> engines = new ArrayList<>();
            for ( int i = 0; i < callers; i++ )
            {
                engines.add( threads.submit( () -> {
                    barrier.await( 30, TimeUnit.SECONDS );
                    return scriptRuntimeFactory.sharedEngine();
                } ) );
            }

            final Engine engine = engines.get( 0 ).get( 30, TimeUnit.SECONDS );
            assertNotNull( engine );
            for ( final Future<Engine> other : engines )
            {
                assertSame( engine, other.get( 30, TimeUnit.SECONDS ) );
            }
        }
        finally
        {
            threads.shutdownNow();
            scriptRuntimeFactory.destroy();
        }
    }

    @Test
    void dispose_closesTheRuntime()
    {
        final ScriptRuntimeFactoryImpl scriptRuntimeFactory =
            spy( new ScriptRuntimeFactoryImpl( bundleContext, resourceService ) );

        final ScriptRuntimeImpl scriptRuntime = mock( ScriptRuntimeImpl.class );
        when( scriptRuntimeFactory.doCreate( any() ) ).thenReturn( scriptRuntime );

        scriptRuntimeFactory.create( ScriptSettings.create().build() );
        scriptRuntimeFactory.dispose( scriptRuntime );

        verify( scriptRuntime ).close();
    }

    @Test
    void destroy_closesLeftoverRuntimes()
    {
        final ScriptRuntimeFactoryImpl scriptRuntimeFactory =
            spy( new ScriptRuntimeFactoryImpl( bundleContext, resourceService ) );

        final ScriptRuntimeImpl disposed = mock( ScriptRuntimeImpl.class );
        final ScriptRuntimeImpl leftover = mock( ScriptRuntimeImpl.class );
        when( scriptRuntimeFactory.doCreate( any() ) ).thenReturn( disposed, leftover );

        scriptRuntimeFactory.create( ScriptSettings.create().build() );
        scriptRuntimeFactory.create( ScriptSettings.create().build() );
        scriptRuntimeFactory.dispose( disposed );

        scriptRuntimeFactory.destroy();

        // the leftover runtime is swept exactly once; the disposed one is not closed again
        verify( disposed ).close();
        verify( leftover ).close();
        scriptRuntimeFactory.destroy();
        verify( disposed ).close();
        verify( leftover ).close();
    }
}
