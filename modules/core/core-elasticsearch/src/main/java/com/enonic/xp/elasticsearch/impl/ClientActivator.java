package com.enonic.xp.elasticsearch.impl;

import java.util.Hashtable;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.ClusterNodes;
import com.enonic.xp.cluster.ClusterProvider;
import com.enonic.xp.cluster.ClusterProviderHealth;
import com.enonic.xp.cluster.ClusterProviderId;

@Component(immediate = true)
public final class ClientActivator
    implements ClusterProvider
{
    private final ClusterProviderId id = ClusterProviderId.from( "elasticsearch" );

    private final String CLUSTER_HEALTH_TIMEOUT = "5s";

    private Node node;

    private BundleContext context;

    protected ServiceRegistration<Client> reg;

    private final static Logger LOG = LoggerFactory.getLogger( ClientActivator.class );

    @Activate
    @SuppressWarnings("WeakerAccess")
    public void activate( final BundleContext context )
    {
        this.context = context;
        this.reg = null;
    }

    @Deactivate
    @SuppressWarnings("unused")
    public void deactivate()
    {
        unregisterClient();
    }

    @Override
    public ClusterProviderId getId()
    {
        return id;
    }

    @Override
    public ClusterProviderHealth getHealth()
    {
        final ClusterHealthResponse healthResponse = doGetHealth();
        return getProviderState( healthResponse.getStatus() );
    }

    @Override
    public ClusterNodes getNodes()
    {
        return null;
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

        LOG.info( "Cluster operational, register elasticsearch-client" );
        this.reg = this.context.registerService( Client.class, this.node.client(), new Hashtable<>() );
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

    private boolean isRedState()
    {
        try
        {
            final ClusterHealthResponse response = doGetHealth();

            final boolean isRed = response.getStatus() == ClusterHealthStatus.RED;

            if ( isRed )
            {
                LOG.error( "Cluster health in state 'RED' " );
            }

            return isRed;
        }
        catch ( final Exception e )
        {
            LOG.error( "Cluster health in state 'RED' ", e );
            return true;
        }
    }

    private ClusterHealthResponse doGetHealth()
    {
        return this.node.client().admin().cluster().health( new ClusterHealthRequest().
            timeout( CLUSTER_HEALTH_TIMEOUT ).
            waitForYellowStatus() ).
            actionGet();
    }

    private ClusterProviderHealth getProviderState( final ClusterHealthStatus status )
    {
        if ( status == ClusterHealthStatus.RED )
        {
            return ClusterProviderHealth.RED;
        }

        if ( status == ClusterHealthStatus.YELLOW )
        {
            return ClusterProviderHealth.YELLOW;
        }

        return ClusterProviderHealth.GREEN;
    }


    @Reference
    public void setNode( final Node node )
    {
        this.node = node;
    }
}
