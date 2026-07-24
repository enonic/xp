package com.enonic.xp.script.impl.standard;

import java.io.Closeable;
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
        return new ScriptRuntimeImpl( scriptExecutorFactory );
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
    void execute_rearmsBootstrapAfterInvalidate()
    {
        mainScriptExists( true );
        final ScriptRuntimeImpl runtime = runtime();

        runtime.bootstrap( params() );
        runtime.invalidate( APP );
        // the executor incarnation the bootstrap armed is gone (app reconfigure): a top-level
        // execution re-arms the lazily recreated one instead of waiting out the gate timeout
        runtime.execute( CONTROLLER );

        verify( scriptExecutor, times( 2 ) ).bootstrap( MAIN );
        verify( scriptExecutor ).executeMain( CONTROLLER );
    }

    @Test
    void executeBackground_waitsForBootstrapAndSkipsPooledExecution()
    {
        mainScriptExists( true );
        controllerScriptExists( true );
        final ScriptRuntimeImpl runtime = runtime();

        runtime.bootstrap( params() );
        runtime.executeBackground( CONTROLLER, "run" );

        final InOrder inOrder = Mockito.inOrder( scriptExecutor );
        inOrder.verify( scriptExecutor ).bootstrap( MAIN );
        inOrder.verify( scriptExecutor ).executeBackground( CONTROLLER, "run" );
        verify( scriptExecutor, never() ).executeMain( CONTROLLER );
    }

    @Test
    void executeBackground_missingScriptFailsAtResolve()
    {
        mainScriptExists( true );
        controllerScriptExists( false );
        final ScriptRuntimeImpl runtime = runtime();

        runtime.bootstrap( params() );
        // uniform across engines: the existence check fires ahead of the engine-specific
        // require machinery, with one exception type
        Assertions.assertThrows( ResourceNotFoundException.class, () -> runtime.executeBackground( CONTROLLER, "run" ) );
        verify( scriptExecutor, never() ).executeBackground( CONTROLLER, "run" );
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

        final ScriptRuntimeImpl runtime = new ScriptRuntimeImpl( scriptExecutorFactory );
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

        final ScriptRuntimeImpl runtime = new ScriptRuntimeImpl( scriptExecutorFactory );

        Assertions.assertTrue( runtime.hasScript( MAIN ) );
    }

    @Test
    void has_script_app_not_found()
    {
        final ScriptRuntimeImpl runtime = new ScriptRuntimeImpl( scriptExecutorFactory );
        when( scriptExecutorFactory.apply( APP ) ).thenThrow( AppNotRegisteredException.class );

        Assertions.assertFalse( runtime.hasScript( MAIN ) );
    }
}
