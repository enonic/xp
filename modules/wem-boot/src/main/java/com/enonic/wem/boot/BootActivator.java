package com.enonic.wem.boot;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public final class BootActivator
    implements BundleActivator
{
    @Override
    public void start( final BundleContext context )
        throws Exception
    {
        System.out.println( "Start... " + context );
    }

    @Override
    public void stop( final BundleContext context )
        throws Exception
    {
        System.out.println( "Stop... " + context );
    }
}
