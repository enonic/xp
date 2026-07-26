package com.enonic.xp.script.impl.executor;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.impl.function.ApplicationInfoBuilder;
import com.enonic.xp.script.impl.service.ServiceRegistry;
import com.enonic.xp.script.runtime.ScriptSettings;
import com.enonic.xp.util.Version;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScriptExecutorImplTest
{
    private ScriptExecutorImpl executor;

    @BeforeEach
    void setUp()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );
        this.executor = new ScriptExecutorImpl( getClass().getClassLoader(), ScriptSettings.create().build(),
                                                Mockito.mock( ServiceRegistry.class ), Mockito.mock( ResourceService.class ),
                                                new ApplicationInfoBuilder( applicationKey, ConfigBuilder.create().build(),
                                                                            Version.emptyVersion ) );
    }

    @Test
    void executeMethodRequiresTheMethod()
    {
        executor.registerMock( "/test/background.js", "the-exports" );

        // executeMethod runs on the single shared context; a missing method fails loudly -
        // executeMethod's null is a legal scalar-contract result, so it cannot signal this
        assertThrows( IllegalArgumentException.class,
                      () -> executor.executeMethod( ResourceKey.from( "myapplication:/test/background.js" ), "run" ) );
    }

    @Test
    void throwingDisposerDoesNotStopTheRest()
    {
        final AtomicInteger recorder = new AtomicInteger();
        executor.registerDisposer( ResourceKey.from( "myapplication:/a.js" ), () -> {
            throw new IllegalStateException( "boom" );
        } );
        // deeply recursive user JS surfaces as a StackOverflowError — best-effort, like exceptions
        executor.registerDisposer( ResourceKey.from( "myapplication:/b.js" ), () -> {
            throw new StackOverflowError();
        } );
        executor.registerDisposer( ResourceKey.from( "myapplication:/c.js" ), recorder::incrementAndGet );

        assertDoesNotThrow( executor::runDisposers );
        assertEquals( 1, recorder.get() );
    }
}
