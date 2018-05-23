package com.enonic.xp.init;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import com.enonic.xp.exception.InitializationException;

public class InitializerTest
{
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testNonMaster()
    {
        expectedException.expect( InitializationException.class );
        expectedException.expectMessage( "Initialization test not initialized by master node" );

        final Runnable doInitalization = Mockito.mock( Runnable.class );
        createInitializer( false, false, doInitalization ).
            initialize();
    }

    @Test
    public void testMaster()
    {
        final Runnable doInitalization = Mockito.mock( Runnable.class );
        createInitializer( true, false, doInitalization ).
            initialize();
        Mockito.verify( doInitalization ).run();
    }

    @Test
    public void testNonMasterInitialized()
    {
        final Runnable doInitalization = Mockito.mock( Runnable.class );
        createInitializer( false, true, doInitalization ).
            initialize();
        Mockito.verify( doInitalization, Mockito.never() ).run();
    }

    @Test
    public void testMasterInitialized()
    {
        final Runnable doInitalization = Mockito.mock( Runnable.class );
        createInitializer( true, true, doInitalization ).
            initialize();
        Mockito.verify( doInitalization, Mockito.never() ).run();
    }

    private Initializer createInitializer( final boolean isMaster, final boolean isInitialized, final Runnable initialization )
    {
        return TestInitializer.create().
            setInitializationCheckMaxCount( 2l ).
            setInitializationCheckPeriod( 1l ).
            setMaster( isMaster ).
            setInitialized( isInitialized ).setInitialization( initialization ).
            build();
    }
}
