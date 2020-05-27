package com.enonic.xp.script.impl.standard;

import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.impl.executor.ScriptExecutor;

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
    void runDisposers()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myApp" );
        when( scriptExecutorFactory.apply( applicationKey ) ).thenReturn( scriptExecutor );

        final ScriptRuntimeImpl scriptRuntime = new ScriptRuntimeImpl( scriptExecutorFactory );
        final ResourceKey resourceKey = ResourceKey.from( applicationKey, "/main.js" );

        scriptRuntime.execute( resourceKey );

        scriptRuntime.runDisposers( applicationKey );

        verify( scriptExecutor, Mockito.times( 1 ) ).runDisposers();
    }

    @Test
    void runDisposers_no_script_executor()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myApp" );
        when( scriptExecutorFactory.apply( applicationKey ) ).thenReturn( scriptExecutor );

        final ScriptRuntimeImpl scriptRuntime = new ScriptRuntimeImpl( scriptExecutorFactory );
        final ResourceKey resourceKey = ResourceKey.from( applicationKey, "/main.js" );

        scriptRuntime.execute( resourceKey );

        scriptRuntime.invalidate( applicationKey );

        scriptRuntime.runDisposers( applicationKey );

        verify( scriptExecutor, Mockito.never() ).runDisposers();
    }
}