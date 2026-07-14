package com.enonic.xp.script.impl.standard;

import java.util.function.Function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.impl.AppNotRegisteredException;
import com.enonic.xp.script.impl.executor.ScriptExecutor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScriptRuntimeImplTest
{
    @Mock
    Function<ApplicationKey, ScriptExecutor> scriptExecutorFactory;

    @Mock
    ScriptExecutor scriptExecutor;

    @Test
    void executeAsync()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myApp" );
        when( scriptExecutorFactory.apply( applicationKey ) ).thenReturn( scriptExecutor );

        final ScriptRuntimeImpl scriptRuntime = new ScriptRuntimeImpl( scriptExecutorFactory );
        final ResourceKey resourceKey = ResourceKey.from( applicationKey, "/main.js" );

        scriptRuntime.executeAsync( resourceKey );

        verify( scriptExecutor, Mockito.times( 1 ) ).executeMainAsync( resourceKey );
    }

    @Test
    void execute()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myApp" );
        when( scriptExecutorFactory.apply( applicationKey ) ).thenReturn( scriptExecutor );

        final ScriptRuntimeImpl scriptRuntime = new ScriptRuntimeImpl( scriptExecutorFactory );
        final ResourceKey resourceKey = ResourceKey.from( applicationKey, "/main.js" );

        scriptRuntime.execute( resourceKey );

        verify( scriptExecutor, Mockito.times( 1 ) ).executeMain( resourceKey );
    }

    @Test
    void invalidate_runsDisposersOfTheRemovedExecutor()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myApp" );
        when( scriptExecutorFactory.apply( applicationKey ) ).thenReturn( scriptExecutor );

        final ScriptRuntimeImpl scriptRuntime = new ScriptRuntimeImpl( scriptExecutorFactory );
        scriptRuntime.execute( ResourceKey.from( applicationKey, "/main.js" ) );

        scriptRuntime.invalidate( applicationKey );
        // idempotent: the executor is gone, nothing to dispose twice
        scriptRuntime.invalidate( applicationKey );

        verify( scriptExecutor, Mockito.times( 1 ) ).runDisposers();
    }

    @Test
    void invalidate_runsDisposersBeforeClosingTheExecutor()
        throws Exception
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myApp" );
        final ScriptExecutor closeableExecutor =
            mock( ScriptExecutor.class, Mockito.withSettings().extraInterfaces( java.io.Closeable.class ) );
        when( scriptExecutorFactory.apply( applicationKey ) ).thenReturn( closeableExecutor );

        final ScriptRuntimeImpl scriptRuntime = new ScriptRuntimeImpl( scriptExecutorFactory );
        scriptRuntime.execute( ResourceKey.from( applicationKey, "/main.js" ) );

        scriptRuntime.invalidate( applicationKey );

        final org.mockito.InOrder inOrder = Mockito.inOrder( closeableExecutor );
        inOrder.verify( closeableExecutor ).runDisposers();
        inOrder.verify( (java.io.Closeable) closeableExecutor ).close();
    }

    @Test
    void has_script()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myApp" );
        when( scriptExecutorFactory.apply( applicationKey ) ).thenReturn( scriptExecutor );

        final ResourceService resourceService = mock( ResourceService.class );
        when( scriptExecutor.getResourceService() ).thenReturn( resourceService );

        final ScriptRuntimeImpl scriptRuntime = new ScriptRuntimeImpl( scriptExecutorFactory );
        final ResourceKey resourceKey = ResourceKey.from( applicationKey, "/main.js" );

        final Resource resource = mock( Resource.class );
        when( resourceService.getResource( resourceKey ) ).thenReturn( resource );

        when( resource.exists() ).thenReturn( true );

        Assertions.assertTrue( scriptRuntime.hasScript( resourceKey ) );
    }

    @Test
    void has_script_app_not_found()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myApp" );
        final ResourceKey resourceKey = ResourceKey.from( applicationKey, "/main.js" );

        final ScriptRuntimeImpl scriptRuntime = new ScriptRuntimeImpl( scriptExecutorFactory );
        when( scriptExecutorFactory.apply( applicationKey ) ).thenThrow( AppNotRegisteredException.class );

        Assertions.assertFalse( scriptRuntime.hasScript( resourceKey ) );
    }
}
