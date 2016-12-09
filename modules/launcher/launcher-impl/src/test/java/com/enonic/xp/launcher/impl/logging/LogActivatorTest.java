package com.enonic.xp.launcher.impl.logging;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

public class LogActivatorTest
{
    private LogActivator activator;

    @Before
    public void setup()
    {
        this.activator = new LogActivator();
    }

    @Test
    public void testLifecycle()
        throws Exception
    {
        final BundleContext context = Mockito.mock( BundleContext.class );
        this.activator.start( context );

        Mockito.verify( context, Mockito.times( 1 ) ).registerService( LogService.class.getName(), LogServiceFactory.INSTANCE, null );

        this.activator.stop( context );
    }
}
