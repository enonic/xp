package com.enonic.xp.ignite.impl;

import java.util.Hashtable;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.cluster.ClusterNode;
import com.enonic.xp.cluster.ClusterNodes;
import com.enonic.xp.cluster.ClusterProvider;
import com.enonic.xp.cluster.ClusterProviderHealth;
import com.enonic.xp.cluster.ClusterProviderId;
import com.enonic.xp.ignite.impl.config.ConfigurationFactory;
import com.enonic.xp.ignite.impl.config.IgniteSettings;

@Component(immediate = true, configurationPid = "com.enonic.xp.ignite")
public class IgniteActivator
    implements ClusterProvider
{
    private Ignite ignite;

    private ServiceRegistration<Ignite> reg;

    private BundleContext context;

    private static Logger LOG = LoggerFactory.getLogger( IgniteActivator.class );

    private IgniteSettings igniteSettings;

    private ClusterConfig clusterConfig;

    @SuppressWarnings("unused")
    @Activate
    public void activate( final BundleContext context, final IgniteSettings igniteSettings )
    {
        this.context = context;
        this.igniteSettings = igniteSettings;

        final IgniteConfiguration igniteConfig = ConfigurationFactory.create().
            clusterConfig( this.clusterConfig ).
            igniteConfig( this.igniteSettings ).
            build().
            execute();

        this.ignite = Ignition.start( igniteConfig );
    }

    @SuppressWarnings("unused")
    @Deactivate
    public void deactivate()
    {
        this.ignite.close();
        unregisterClient();
    }

    @Override
    public ClusterProviderId getId()
    {
        return ClusterProviderId.from( "ignite" );
    }

    @Override
    public ClusterProviderHealth getHealth()
    {
        return ClusterProviderHealth.GREEN;
    }

    @Override
    public ClusterNodes getNodes()
    {
        final ClusterNodes.Builder builder = ClusterNodes.create();

        this.ignite.cluster().nodes().forEach( node -> builder.add( ClusterNode.from( node.consistentId().toString() ) ) );

        return builder.build();
    }

    @Override
    public void enable()
    {
        registerClient();
    }

    @Override
    public void disable()
    {
        unregisterClient();
    }

    private void registerClient()
    {
        if ( this.reg != null )
        {
            return;
        }

        LOG.info( "Cluster operational, register " + this.getId() );

        this.reg = context.registerService( Ignite.class, ignite, new Hashtable<>() );
    }

    private void unregisterClient()
    {
        if ( this.reg == null )
        {
            return;
        }

        try
        {
            LOG.info( "Cluster not operational, unregister elasticsearch-client" );
            this.reg.unregister();
        }
        finally
        {
            this.reg = null;
        }
    }

    @SuppressWarnings("unused")
    @Reference
    public void setClusterConfig( final ClusterConfig clusterConfig )
    {
        this.clusterConfig = clusterConfig;
    }
}
