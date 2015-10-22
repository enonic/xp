package com.enonic.xp.jmx.impl;

import java.lang.management.ManagementFactory;
import java.util.Hashtable;

import javax.management.MBeanServer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component(immediate = true)
public final class MBeanExporter
{
    private ServiceRegistration reg;

    @Activate
    public void activate( final BundleContext context )
    {
        final Hashtable<String, Object> config = new Hashtable<>();
        final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        this.reg = context.registerService( MBeanServer.class, server, config );
    }

    @Deactivate
    public void deactivate()
    {
        if ( this.reg != null )
        {
            this.reg.unregister();
        }
    }
}
