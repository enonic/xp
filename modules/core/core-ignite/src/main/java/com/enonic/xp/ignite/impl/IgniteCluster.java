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

@Component(immediate = true, configurationPid = "com.enonic.xp.ignite")
public class IgniteCluster
    implements Cluster
{
    private Ignite ignite;

    private ServiceRegistration<Ignite> reg;

    private BundleContext context;

    private static final Logger LOG = LoggerFactory.getLogger( IgniteCluster.class );

    private IgniteSettings igniteSettings;

    private ClusterConfig clusterConfig;

    @SuppressWarnings("unused")
    @Activate
    public void activate( final BundleContext context, final IgniteSettings igniteSettings )
    {
        this.context = context;
        this.igniteSettings = igniteSettings;

        adjustLoggingVerbosity();

        final IgniteConfiguration igniteConfig = ConfigurationFactory.create().
            clusterConfig( this.clusterConfig ).
            igniteConfig( this.igniteSettings ).
            bundleContext( context ).
            build().
            execute();

        System.setProperty( IGNITE_NO_SHUTDOWN_HOOK, "true" );
        this.ignite = Ignition.start( igniteConfig );

        // Register admin-client to use in e.g reporting
        context.registerService( IgniteAdminClient.class, new IgniteAdminClientImpl( this.ignite ), new Hashtable<>() );
    }

    private void adjustLoggingVerbosity()
    {
        System.setProperty( IGNITE_NO_ASCII, "false" );
        System.setProperty( IGNITE_PERFORMANCE_SUGGESTIONS_DISABLED, "true" );
        System.setProperty( IGNITE_UPDATE_NOTIFIER, "false" );
        System.setProperty( IGNITE_TROUBLESHOOTING_LOGGER, "false" );
    }

    @SuppressWarnings("unused")
    @Deactivate
    public void deactivate()
    {
        this.ignite.close();
        unregisterClient();
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
        unregisterClient();
    }

    @Override
    public boolean isEnabled()
    {
        return this.reg != null;
    }

    private void registerService()
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
            LOG.info( "Cluster not operational, unregister " + this.getId() );
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
