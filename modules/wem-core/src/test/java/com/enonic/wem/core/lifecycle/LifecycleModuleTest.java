package com.enonic.wem.core.lifecycle;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import static org.junit.Assert.*;

public class LifecycleModuleTest
{
    @Inject
    private LifecycleManager manager;

    @Inject
    private Injector injector;

    @Before
    public void setUp()
    {
        Guice.createInjector( new LifecycleModule() ).injectMembers( this );
    }

    @Test
    public void testInitDispose()
    {
        final LifecycleService1 service = this.injector.getInstance( LifecycleService1.class );
        assertTrue( service.initialized );
        assertFalse( service.disposed );

        this.manager.dispose();
        assertTrue( service.disposed );
    }
}
