package com.enonic.xp.ignite.impl;

import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.cluster.Cluster;
import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.ignite.impl.config.IgniteSettings;

@Component(immediate = true, configurationPid = "com.enonic.xp.ignite")
public class IgniteClusterActivator
{
    private ServiceRegistration<Cluster> reg;

    private IgniteCluster igniteCluster;

    private ClusterConfig clusterConfig;

    @SuppressWarnings("unused")
    @Activate
    public void activate( final BundleContext context, final IgniteSettings igniteSettings )
    {
        if ( !igniteSettings.enabled() )
        {
            return;
        }

        igniteCluster = new IgniteCluster( context, igniteSettings, this.clusterConfig );
        reg = context.registerService( Cluster.class, igniteCluster, new Hashtable<>() );
    }

    @SuppressWarnings("unused")
    @Deactivate
    public void deactivate()
    {
        if ( this.reg != null )
        {
            try
            {
                this.reg.unregister();
            }
            finally
            {
                this.reg = null;
            }
        }
        if ( igniteCluster != null )
        {
            igniteCluster.deactivate();
        }
    }

    @SuppressWarnings("unused")
    @Reference
    public void setClusterConfig( final ClusterConfig clusterConfig )
    {
        this.clusterConfig = clusterConfig;
    }
}
