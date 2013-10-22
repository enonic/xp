package com.enonic.wem.core.lifecycle;

import org.junit.Test;

import static org.junit.Assert.*;

public class LifecycleBeanTest
{
    @Test
    public void testBasic()
    {
        final LifecycleTestingBean bean = new LifecycleTestingBean( RunLevel.L2 );
        assertEquals( "LifecycleTestingBean", bean.getName() );
        assertEquals( RunLevel.L2, bean.getRunLevel() );
        assertEquals( false, bean.isRunning() );
    }

    @Test
    public void testStart()
        throws Exception
    {
        final LifecycleTestingBean bean = new LifecycleTestingBean( RunLevel.L2 );
        assertEquals( 0, bean.startCount );
        assertEquals( false, bean.isRunning() );

        bean.start();
        assertEquals( 1, bean.startCount );
        assertEquals( true, bean.isRunning() );

        bean.start();
        assertEquals( 1, bean.startCount );
        assertEquals( true, bean.isRunning() );
    }

    @Test
    public void testStop()
        throws Exception
    {
        final LifecycleTestingBean bean = new LifecycleTestingBean( RunLevel.L2 );
        assertEquals( 0, bean.stopCount );
        assertEquals( false, bean.isRunning() );

        bean.stop();
        assertEquals( 0, bean.stopCount );
        assertEquals( false, bean.isRunning() );

        bean.start();
        assertEquals( 1, bean.startCount );
        assertEquals( true, bean.isRunning() );

        bean.stop();
        assertEquals( 1, bean.stopCount );
        assertEquals( false, bean.isRunning() );

        bean.stop();
        assertEquals( 1, bean.stopCount );
        assertEquals( false, bean.isRunning() );
    }

    @Test
    public void testStart_error()
        throws Exception
    {
        final LifecycleTestingBean bean = new LifecycleTestingBean( RunLevel.L2 );
        bean.exceptionOnStart = true;

        assertEquals( 0, bean.startCount );
        assertEquals( false, bean.isRunning() );

        try
        {
            bean.start();
            fail( "Should throw exception" );
        }
        catch ( final Exception e )
        {
            assertEquals( 1, bean.startCount );
            assertEquals( false, bean.isRunning() );
        }
    }

    @Test
    public void testStop_error()
        throws Exception
    {
        final LifecycleTestingBean bean = new LifecycleTestingBean( RunLevel.L2 );
        bean.exceptionOnStop = true;

        bean.start();
        assertEquals( true, bean.isRunning() );
        assertEquals( 0, bean.stopCount );

        bean.stop();
        assertEquals( 1, bean.stopCount );
        assertEquals( false, bean.isRunning() );
    }
}
