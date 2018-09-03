package com.enonic.xp.elasticsearch.impl;

import java.util.Hashtable;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.node.Node;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.Cluster;
import com.enonic.xp.cluster.ClusterHealth;
import com.enonic.xp.cluster.ClusterHealthStatus;
import com.enonic.xp.cluster.ClusterId;
import com.enonic.xp.cluster.ClusterNodes;

@Component(immediate = true)
public final class ElasticsearchCluster
    implements Cluster
{
    private final ClusterId id = ClusterId.from( "elasticsearch" );

    private final String CLUSTER_HEALTH_TIMEOUT = "5s";

    private Node node;

    private BundleContext context;

    protected ServiceRegistration<Client> reg;

    private final static Logger LOG = LoggerFactory.getLogger( ElasticsearchCluster.class );

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
    public ClusterId getId()
    {
        return id;
    }

    @Override
    public boolean isEnabled()
    {
        return this.reg != null;
    }

    @Override
    public ClusterHealth getHealth()
    {
        try
        {
            final ClusterHealthResponse healthResponse = doGetHealth();
            return toClusterHealth( healthResponse.getStatus() );
        }
        catch ( Exception e )
        {
            return ClusterHealth.create().
                status( ClusterHealthStatus.RED ).
                errorMessage( e.getClass().getSimpleName() + "[" + e.getMessage() + "]" ).
                build();
        }
    }

    @Override
    public ClusterNodes getNodes()
    {
        try
        {
            final DiscoveryNodes members = getMembers();
            return ClusterNodesFactory.create( members );
        }
        catch ( Exception e )
        {
            return ClusterNodes.create().build();
        }
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

    private ClusterHealthResponse doGetHealth()
    {
        return this.node.client().admin().cluster().health( new ClusterHealthRequest().
            timeout( CLUSTER_HEALTH_TIMEOUT ).
            waitForYellowStatus() ).
            actionGet();
    }

    private DiscoveryNodes getMembers()
    {
        final ClusterStateRequest clusterStateRequest = Requests.clusterStateRequest().
            clear().
            nodes( true ).
            indices( "" ).
            masterNodeTimeout( CLUSTER_HEALTH_TIMEOUT );

        final ClusterStateResponse response = this.node.client().admin().cluster().state( clusterStateRequest ).actionGet();

        return response.getState().getNodes();
    }

    private ClusterHealth toClusterHealth( final org.elasticsearch.cluster.health.ClusterHealthStatus status )
    {
        if ( status == org.elasticsearch.cluster.health.ClusterHealthStatus.RED )
        {
            return ClusterHealth.red();
        }

        if ( status == org.elasticsearch.cluster.health.ClusterHealthStatus.YELLOW )
        {
            return ClusterHealth.yellow();
        }

        return ClusterHealth.green();
    }


    @Reference
    public void setNode( final Node node )
    {
        this.node = node;
    }
}
