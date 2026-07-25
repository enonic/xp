package com.enonic.xp.script.impl.standard;

import java.io.Closeable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceNotFoundException;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.impl.AppNotRegisteredException;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.runtime.BootstrapParams;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScriptRuntimeImplTest
{
    private static final ApplicationKey APP = ApplicationKey.from( "myApp" );

    private static final ResourceKey MAIN = ResourceKey.from( APP, "/main.js" );

    private static final ResourceKey CONTROLLER = ResourceKey.from( APP, "/controller.js" );

    @Mock
    Function<ApplicationKey, ScriptExecutor> scriptExecutorFactory;

    @Mock
    ScriptExecutor scriptExecutor;

    @Mock
    ResourceService resourceService;

    private final Object incarnation = new Object();

    private static BootstrapParams params()
    {
        return BootstrapParams.create().application( APP ).mainScript( MAIN ).build();
    }

    private static BootstrapParams paramsWithoutScript()
    {
        return BootstrapParams.create().application( APP ).build();
    }

    private void mainScriptExists( final boolean exists )
    {
        final Resource resource = mock( Resource.class );
        lenient().when( resource.exists() ).thenReturn( exists );
        lenient().when( scriptExecutor.getResourceService() ).thenReturn( resourceService );
        lenient().when( resourceService.getResource( MAIN ) ).thenReturn( resource );
    }

    private ScriptRuntimeImpl runtime()
    {
        when( scriptExecutorFactory.apply( APP ) ).thenReturn( scriptExecutor );
        return new ScriptRuntimeImpl( scriptExecutorFactory, key -> incarnation );
    }

    @Test
    void bootstrap_runsMainScriptOnce()
    {
        mainScriptExists( true );
        final ScriptRuntimeImpl runtime = runtime();

        runtime.bootstrap( params() );
        runtime.bootstrap( params() );

        verify( scriptExecutor, times( 1 ) ).bootstrap( MAIN );
    }

    @Test
    void bootstrap_withoutMainScript_opensGateWithoutExecuting()
    {
        final ScriptRuntimeImpl runtime = runtime();

        runtime.bootstrap( paramsWithoutScript() );
        // gate is open: a controller runs without waiting, and no bootstrap script was executed
        runtime.execute( CONTROLLER );

        verify( scriptExecutor, never() ).bootstrap( MAIN );
        verify( scriptExecutor ).executeMain( CONTROLLER );
    }

    @Test
    void execute_waitsForBootstrapThenRunsController()
    {
        mainScriptExists( true );
        final ScriptRuntimeImpl runtime = runtime();

        runtime.bootstrap( params() );
        runtime.execute( CONTROLLER );

        final InOrder inOrder = Mockito.inOrder( scriptExecutor );
        inOrder.verify( scriptExecutor ).bootstrap( MAIN );
        inOrder.verify( scriptExecutor ).executeMain( CONTROLLER );
    }

    @Test
    void execute_bootstrapsOnlyOnceAcrossControllers()
    {
        mainScriptExists( true );
        final ScriptRuntimeImpl runtime = runtime();

        runtime.bootstrap( params() );
        runtime.execute( CONTROLLER );
        runtime.execute( CONTROLLER );

        verify( scriptExecutor, times( 1 ) ).bootstrap( MAIN );
        verify( scriptExecutor, times( 2 ) ).executeMain( CONTROLLER );
    }

    @Test
    void bootstrap_mainScriptError_stillOpensGate()
    {
        mainScriptExists( true );
        final ScriptRuntimeImpl runtime = runtime();
        when( scriptExecutor.bootstrap( MAIN ) ).thenThrow( new RuntimeException( "boom" ) );

        runtime.bootstrap( params() );
        // a broken bootstrap script must not dam the application: the controller still runs
        runtime.execute( CONTROLLER );

        verify( scriptExecutor ).executeMain( CONTROLLER );
    }

    @Test
    void execute_reentrantDuringBootstrap_skipsTheGate()
    {
        mainScriptExists( true );
        final ScriptRuntimeImpl runtime = runtime();
        // the bootstrap script synchronously invokes another of the app's scripts: the re-entrant
        // execution must run instead of waiting for the gate it is itself about to open
        when( scriptExecutor.bootstrap( MAIN ) ).thenAnswer( invocation -> {
            runtime.execute( CONTROLLER );
            return null;
        } );

        runtime.bootstrap( params() );

        verify( scriptExecutor ).bootstrap( MAIN );
        verify( scriptExecutor ).executeMain( CONTROLLER );
    }

    @Test
    void staleIncarnation_diesOnNextUse()
    {
        mainScriptExists( true );
        final AtomicReference<Object> current = new AtomicReference<>( new Object() );
        when( scriptExecutorFactory.apply( APP ) ).thenReturn( scriptExecutor );
        final ScriptRuntimeImpl runtime = new ScriptRuntimeImpl( scriptExecutorFactory, key -> current.get() );

        runtime.bootstrap( params() );
        runtime.execute( CONTROLLER );

        // the application was replaced (new service registration) and this runtime's teardown
        // raced the creation: the executor stamped with the old incarnation is torn down on its
        // next touch and rebuilt from the current one — here armed again by the new bootstrap
        current.set( new Object() );
        runtime.bootstrap( params() );
        runtime.execute( CONTROLLER );

        verify( scriptExecutorFactory, times( 2 ) ).apply( APP );
        // the stale executor got a full instance teardown
        verify( scriptExecutor ).runDisposers();
        verify( scriptExecutor, times( 2 ) ).bootstrap( MAIN );
        verify( scriptExecutor, times( 2 ) ).executeMain( CONTROLLER );
    }

    @Test
    void waiterReleasedByInvalidate_failsFastInsteadOfExecuting()
        throws Exception
    {
        final AtomicReference<Object> current = new AtomicReference<>( new Object() );
        when( scriptExecutorFactory.apply( APP ) ).thenReturn( scriptExecutor ).thenThrow( new AppNotRegisteredException() );
        final ScriptRuntimeImpl runtime = new ScriptRuntimeImpl( scriptExecutorFactory, key -> current.get() );

        final ExecutorService thread = Executors.newSingleThreadExecutor();
        try
        {
            // no bootstrap has run: the execution parks at the application's gate
            final Future<?> execution = thread.submit( () -> runtime.execute( CONTROLLER ) );
            Assertions.assertThrows( TimeoutException.class, () -> execution.get( 500, TimeUnit.MILLISECONDS ) );

            // the application stops: teardown opens the gate to release waiters, and the released
            // waiter must fail fast instead of executing on the torn-down executor
            current.set( null );
            runtime.invalidate( APP );

            final ExecutionException e =
                Assertions.assertThrows( ExecutionException.class, () -> execution.get( 10, TimeUnit.SECONDS ) );
            Assertions.assertInstanceOf( AppNotRegisteredException.class, e.getCause() );
            verify( scriptExecutor, never() ).executeMain( CONTROLLER );
        }
        finally
        {
            thread.shutdownNow();
        }
    }

    @Test
    void executeMethod_waitsForBootstrapAndSkipsPooledExecution()
    {
        mainScriptExists( true );
        controllerScriptExists( true );
        final ScriptRuntimeImpl runtime = runtime();

        runtime.bootstrap( params() );
        runtime.executeMethod( CONTROLLER, "run" );

        final InOrder inOrder = Mockito.inOrder( scriptExecutor );
        inOrder.verify( scriptExecutor ).bootstrap( MAIN );
        inOrder.verify( scriptExecutor ).executeMethod( CONTROLLER, "run" );
        verify( scriptExecutor, never() ).executeMain( CONTROLLER );
    }

    @Test
    void executeMethod_missingScriptFailsAtResolve()
    {
        mainScriptExists( true );
        controllerScriptExists( false );
        final ScriptRuntimeImpl runtime = runtime();

        runtime.bootstrap( params() );
        // uniform across engines: the existence check fires ahead of the engine-specific
        // require machinery, with one exception type
        Assertions.assertThrows( ResourceNotFoundException.class, () -> runtime.executeMethod( CONTROLLER, "run" ) );
        verify( scriptExecutor, never() ).executeMethod( CONTROLLER, "run" );
    }

    private void controllerScriptExists( final boolean exists )
    {
        final Resource resource = mock( Resource.class );
        lenient().when( resource.exists() ).thenReturn( exists );
        lenient().when( resourceService.getResource( CONTROLLER ) ).thenReturn( resource );
    }

    @Test
    void invalidate_runsDisposersOfTheRemovedExecutor()
    {
        final ScriptRuntimeImpl runtime = runtime();
        runtime.bootstrap( paramsWithoutScript() );

        runtime.invalidate( APP );
        // idempotent: the executor is gone, nothing to dispose twice
        runtime.invalidate( APP );

        verify( scriptExecutor, times( 1 ) ).runDisposers();
    }

    @Test
    void invalidate_runsDisposersBeforeClosingTheExecutor()
        throws Exception
    {
        final ScriptExecutor closeableExecutor =
            mock( ScriptExecutor.class, Mockito.withSettings().extraInterfaces( Closeable.class ) );
        when( scriptExecutorFactory.apply( APP ) ).thenReturn( closeableExecutor );

        final ScriptRuntimeImpl runtime = new ScriptRuntimeImpl( scriptExecutorFactory, key -> incarnation );
        runtime.bootstrap( paramsWithoutScript() );

        runtime.invalidate( APP );

        final InOrder inOrder = Mockito.inOrder( closeableExecutor );
        inOrder.verify( closeableExecutor ).runDisposers();
        inOrder.verify( (Closeable) closeableExecutor ).close();
    }

    @Test
    void has_script()
    {
        when( scriptExecutorFactory.apply( APP ) ).thenReturn( scriptExecutor );
        when( scriptExecutor.getResourceService() ).thenReturn( resourceService );
        final Resource resource = mock( Resource.class );
        when( resourceService.getResource( MAIN ) ).thenReturn( resource );
        when( resource.exists() ).thenReturn( true );

        final ScriptRuntimeImpl runtime = new ScriptRuntimeImpl( scriptExecutorFactory, key -> incarnation );

        Assertions.assertTrue( runtime.hasScript( MAIN ) );
    }

    @Test
    void has_script_app_not_found()
    {
        final ScriptRuntimeImpl runtime = new ScriptRuntimeImpl( scriptExecutorFactory, key -> incarnation );
        when( scriptExecutorFactory.apply( APP ) ).thenThrow( AppNotRegisteredException.class );

        Assertions.assertFalse( runtime.hasScript( MAIN ) );
    }
}
