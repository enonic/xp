package com.enonic.wem.launcher.activator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.service.cm.ConfigurationAdmin;

public final class ConfigDeployer
    implements BundleActivator, ServiceListener
{
    @Override
    public void start( final BundleContext context )
        throws Exception
    {
        context.addServiceListener( this, "(" + Constants.OBJECTCLASS + "=" + ConfigurationAdmin.class.getName() + ")" );
    }

    @Override
    public void stop( final BundleContext context )
        throws Exception
    {
    }

    @Override
    public void serviceChanged( final ServiceEvent event )
    {
        System.out.println( event );
    }
}
