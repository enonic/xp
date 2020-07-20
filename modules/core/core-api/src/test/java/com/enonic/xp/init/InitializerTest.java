package com.enonic.xp.init;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class InitializerTest
{
    @Test
    public void testNonMaster()
    {
        final Runnable doInitalization = Mockito.mock( Runnable.class );

        final long willInitializeIn = 100L;

        final TestInitializer initializer = TestInitializer.create().
            setInitializationCheckMaxCount( willInitializeIn * 2L ).
            setInitializationCheckPeriod( 1L ).
            setMaster( false ).
            setInitialized( false ).
            setInitialization( doInitalization ).
            build();

        CompletableFuture.runAsync( () -> initializer.setInitialized( true ),
                                    CompletableFuture.delayedExecutor( willInitializeIn, TimeUnit.MILLISECONDS ) );
        initializer.initialize();
        Mockito.verify( doInitalization, Mockito.never() ).run();
    }

    @Test
    public void testMaster()
    {
        final Runnable doInitalization = Mockito.mock( Runnable.class );
        final TestInitializer initializer = TestInitializer.create().
            setMaster( true ).
            setInitialized( false ).
            setInitialization( doInitalization ).
            build();

        initializer.initialize();

        Mockito.verify( doInitalization ).run();
    }

    @Test
    public void testNonMasterInitialized()
    {
        final Runnable doInitalization = Mockito.mock( Runnable.class );
        final TestInitializer initializer = TestInitializer.create().
            setMaster( false ).
            setInitialized( true ).
            setInitialization( doInitalization ).
            build();

        initializer.initialize();

        Mockito.verify( doInitalization, Mockito.never() ).run();
    }

    @Test
    public void testMasterInitialized()
    {
        final Runnable doInitalization = Mockito.mock( Runnable.class );
        final TestInitializer initializer = TestInitializer.create().
            setMaster( true ).
            setInitialized( true ).
            setInitialization( doInitalization ).
            build();

        initializer.initialize();

        Mockito.verify( doInitalization, Mockito.never() ).run();
    }
}
