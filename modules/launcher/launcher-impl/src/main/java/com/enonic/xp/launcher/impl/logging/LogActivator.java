package com.enonic.xp.launcher.impl.logging;

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
        context.registerService( LogService.class.getName(), LogServiceFactory.INSTANCE, null );
    }

    @Override
    public void stop( final BundleContext context )
        throws Exception
    {
        // Do nothing
    }
}
