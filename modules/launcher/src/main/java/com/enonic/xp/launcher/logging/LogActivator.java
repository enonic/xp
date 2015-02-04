package com.enonic.xp.launcher.logging;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

public final class LogActivator
    implements BundleActivator
{
    @Override
    public void start( final BundleContext context )
        throws Exception
    {
        context.registerService( LogService.class.getName(), new LogServiceFactory(), null );
    }

    @Override
    public void stop( final BundleContext context )
        throws Exception
    {
        // Do nothing
    }
}
