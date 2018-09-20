package com.enonic.xp.ignite.impl;

import java.util.Hashtable;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.Cluster;
import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.cluster.ClusterHealth;
import com.enonic.xp.cluster.ClusterId;
import com.enonic.xp.cluster.ClusterNode;
import com.enonic.xp.cluster.ClusterNodes;
import com.enonic.xp.ignite.impl.config.ConfigurationFactory;
import com.enonic.xp.ignite.impl.config.IgniteSettings;

import static org.apache.ignite.IgniteSystemProperties.IGNITE_NO_ASCII;
import static org.apache.ignite.IgniteSystemProperties.IGNITE_NO_SHUTDOWN_HOOK;
import static org.apache.ignite.IgniteSystemProperties.IGNITE_PERFORMANCE_SUGGESTIONS_DISABLED;
import static org.apache.ignite.IgniteSystemProperties.IGNITE_TROUBLESHOOTING_LOGGER;
import static org.apache.ignite.IgniteSystemProperties.IGNITE_UPDATE_NOTIFIER;

public class IgniteCluster
    implements Cluster
{
    private Ignite ignite;

    private ServiceRegistration<Ignite> igniteServiceReg;

    private ServiceRegistration<IgniteAdminClient> igniteAdminClientServiceReg;

    private BundleContext context;

    private static final Logger LOG = LoggerFactory.getLogger( IgniteCluster.class );

    public IgniteCluster( final BundleContext context, final IgniteSettings igniteSettings, final ClusterConfig clusterConfig )
    {
        this.context = context;

        adjustLoggingVerbosity();

        final IgniteConfiguration igniteConfig = ConfigurationFactory.create().
            clusterConfig( clusterConfig ).
            igniteConfig( igniteSettings ).
            bundleContext( context ).
            build().
            execute();

        System.setProperty( IGNITE_NO_SHUTDOWN_HOOK, "true" );
        this.ignite = Ignition.start( igniteConfig );
    }

    private void adjustLoggingVerbosity()
    {
        System.setProperty( IGNITE_NO_ASCII, "false" );
        System.setProperty( IGNITE_PERFORMANCE_SUGGESTIONS_DISABLED, "true" );
        System.setProperty( IGNITE_UPDATE_NOTIFIER, "false" );
        System.setProperty( IGNITE_TROUBLESHOOTING_LOGGER, "false" );
    }

    void deactivate()
    {
        unregisterService();
        this.ignite.close();
    }

    @Override
    public ClusterId getId()
    {
        return ClusterId.from( "ignite" );
    }

    @Override
    public ClusterHealth getHealth()
    {
        return ClusterHealth.green();
    }

    @Override
    public ClusterNodes getNodes()
    {
        try
        {
            return doGetNodes();
        }
        catch ( java.lang.IllegalStateException e )
        {
            return ClusterNodes.create().build();
        }
    }

    private ClusterNodes doGetNodes()
    {
        final ClusterNodes.Builder builder = ClusterNodes.create();

        this.ignite.cluster().nodes().forEach( node -> {

            node.addresses();

            builder.add( ClusterNode.from( node.consistentId().toString() ) );
        } );

        return builder.build();
    }

    @Override
    public void enable()
    {
        registerService();
    }

    @Override
    public void disable()
    {
        unregisterService();
    }

    @Override
    public boolean isEnabled()
    {
        return this.igniteServiceReg != null;
    }

    private void registerService()
    {
        if ( this.igniteServiceReg != null )
        {
            return;
        }

        LOG.info( "Cluster operational, register " + this.getId() );

        this.igniteServiceReg = context.registerService( Ignite.class, ignite, new Hashtable<>() );

        // Register admin-client to use in e.g reporting
        this.igniteAdminClientServiceReg =
            context.registerService( IgniteAdminClient.class, new IgniteAdminClientImpl( this.ignite ), new Hashtable<>() );
    }

    private void unregisterService()
    {
        if ( this.igniteServiceReg != null )
        {
            try
            {
                LOG.info( "Cluster not operational, unregister " + this.getId() );
                this.igniteServiceReg.unregister();
            }
            finally
            {
                this.igniteServiceReg = null;
            }
        }

        if ( this.igniteAdminClientServiceReg != null )
        {
            try
            {
                this.igniteAdminClientServiceReg.unregister();
            }
            finally
            {
                this.igniteAdminClientServiceReg = null;
            }
        }
    }
}
