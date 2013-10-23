package com.enonic.wem.core.lifecycle;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Names;

import static org.junit.Assert.*;

public class LifecycleServiceImplTest
{
    @Inject
    private LifecycleService service;

    private LifecycleTestingBean bean1;

    private LifecycleTestingBean bean2;

    @Before
    public void setup()
    {
        this.bean1 = new LifecycleTestingBean( RunLevel.L1 );
        this.bean2 = new LifecycleTestingBean( RunLevel.L2 );

        final Injector injector = Guice.createInjector( new AbstractModule()
        {
            @Override
            protected void configure()
            {
                bind( LifecycleTestingBean.class ).annotatedWith( Names.named( "bean1" ) ).toInstance( bean1 );
                bind( LifecycleTestingBean.class ).annotatedWith( Names.named( "bean2" ) ).toInstance( bean2 );
            }
        } );

        injector.injectMembers( this );
    }

    @Test
    public void testStartAll()
        throws Exception
    {
        assertEquals( 0, this.bean1.startCount );
        assertEquals( false, this.bean1.isRunning() );
        assertEquals( 0, this.bean2.startCount );
        assertEquals( false, this.bean2.isRunning() );

        this.service.startAll();

        assertEquals( 1, this.bean1.startCount );
        assertEquals( true, this.bean1.isRunning() );
        assertEquals( 1, this.bean2.startCount );
        assertEquals( true, this.bean2.isRunning() );
    }

    @Test
    public void testStopAll()
        throws Exception
    {
        this.service.startAll();

        assertEquals( 0, this.bean1.stopCount );
        assertEquals( true, this.bean1.isRunning() );
        assertEquals( 0, this.bean2.stopCount );
        assertEquals( true, this.bean2.isRunning() );

        this.service.stopAll();

        assertEquals( 1, this.bean1.stopCount );
        assertEquals( false, this.bean1.isRunning() );
        assertEquals( 1, this.bean2.stopCount );
        assertEquals( false, this.bean2.isRunning() );
    }

    @Test
    public void testStartAll_error()
        throws Exception
    {
        this.bean2.exceptionOnStart = true;

        assertEquals( 0, this.bean1.startCount );
        assertEquals( false, this.bean1.isRunning() );
        assertEquals( 0, this.bean2.startCount );
        assertEquals( false, this.bean2.isRunning() );

        this.service.startAll();

        assertEquals( 1, this.bean1.startCount );
        assertEquals( false, this.bean1.isRunning() );
        assertEquals( 1, this.bean2.startCount );
        assertEquals( false, this.bean2.isRunning() );
    }
}
