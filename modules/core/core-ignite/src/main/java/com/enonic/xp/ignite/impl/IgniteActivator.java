package com.enonic.xp.ignite.impl;

import java.util.Hashtable;
import java.util.Map;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true, configurationPid = "com.enonic.xp.ignite")
public class IgniteActivator
{
    private ServiceRegistration<Ignite> igniteReg;

    @Activate
    public void activate( final BundleContext context, final Map<String, String> map )
    {
        final IgniteConfiguration config = new IgniteConfiguration();
        config.setDiscoverySpi( DiscoveryFactory.create() );

        final Ignite ignite = Ignition.start();

        System.out.println( " -----------------------------------------------------------------" );
        System.out.println( "------Setting ignite object (ACTIVATOR): " + ignite.hashCode() );
        System.out.println( " -----------------------------------------------------------------" );

        this.igniteReg = context.registerService( Ignite.class, ignite, new Hashtable<>() );
    }
}
