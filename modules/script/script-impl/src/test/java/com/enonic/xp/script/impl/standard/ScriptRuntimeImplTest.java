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
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.impl.AppNotRegisteredException;
import com.enonic.xp.script.impl.executor.ScriptExecutor;

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

        runtime.bootstrap( APP );
        runtime.bootstrap( APP );

        verify( scriptExecutor, times( 1 ) ).executeMain( MAIN );
    }

    @Test
    void bootstrap_noMainScript_doesNotExecute()
    {
        mainScriptExists( false );
        final ScriptRuntimeImpl runtime = runtime();

        runtime.bootstrap( APP );

        verify( scriptExecutor, never() ).executeMain( MAIN );
    }

    @Test
    void execute_bootstrapsBeforeTheController()
    {
        mainScriptExists( true );
        final ScriptRuntimeImpl runtime = runtime();

        runtime.execute( CONTROLLER );

        final InOrder inOrder = Mockito.inOrder( scriptExecutor );
        inOrder.verify( scriptExecutor ).executeMain( MAIN );
        inOrder.verify( scriptExecutor ).executeMain( CONTROLLER );
    }

    @Test
    void execute_bootstrapsOnlyOnceAcrossControllers()
    {
        mainScriptExists( true );
        final ScriptRuntimeImpl runtime = runtime();

        runtime.bootstrap( APP );
        runtime.execute( CONTROLLER );
        runtime.execute( CONTROLLER );

        verify( scriptExecutor, times( 1 ) ).executeMain( MAIN );
        verify( scriptExecutor, times( 2 ) ).executeMain( CONTROLLER );
    }

    @Test
    void executeAsync_bootstrapsFirst()
    {
        mainScriptExists( true );
        final ScriptRuntimeImpl runtime = runtime();

        runtime.executeAsync( CONTROLLER );

        verify( scriptExecutor ).executeMain( MAIN );
        verify( scriptExecutor ).executeMainAsync( CONTROLLER );
    }

    @Test
    void invalidate_runsDisposersOfTheRemovedExecutor()
    {
        mainScriptExists( false );
        final ScriptRuntimeImpl runtime = runtime();
        runtime.bootstrap( APP );

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
        final Resource resource = mock( Resource.class );
        lenient().when( resource.exists() ).thenReturn( false );
        lenient().when( closeableExecutor.getResourceService() ).thenReturn( resourceService );
        lenient().when( resourceService.getResource( MAIN ) ).thenReturn( resource );
        when( scriptExecutorFactory.apply( APP ) ).thenReturn( closeableExecutor );

        final ScriptRuntimeImpl runtime = new ScriptRuntimeImpl( scriptExecutorFactory );
        runtime.bootstrap( APP );

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
