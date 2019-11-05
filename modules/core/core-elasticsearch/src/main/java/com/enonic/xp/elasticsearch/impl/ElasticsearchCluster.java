package com.enonic.xp.elasticsearch.impl;

import java.util.Hashtable;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.metadata.IndexMetaData;
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

import com.google.common.collect.UnmodifiableIterator;

import com.enonic.xp.cluster.Cluster;
import com.enonic.xp.cluster.ClusterHealth;
import com.enonic.xp.cluster.ClusterHealthStatus;
import com.enonic.xp.cluster.ClusterId;
import com.enonic.xp.cluster.ClusterNodes;

@Component(immediate = true)
public final class ElasticsearchCluster
    implements Cluster
{
    private static final Logger LOG = LoggerFactory.getLogger( ElasticsearchCluster.class );

    private static final String CLUSTER_HEALTH_TIMEOUT = "5s";

    private final ClusterId id = ClusterId.from( "elasticsearch" );

    private final Node node;

    private final BundleContext bundleContext;

    private volatile ServiceRegistration<Client> clientServiceRegistration;

    @Activate
    public ElasticsearchCluster( final BundleContext bundleContext, @Reference final Node node )
    {
        this.bundleContext = bundleContext;
        this.node = node;
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
        return this.clientServiceRegistration != null;
    }

    @Override
    public ClusterHealth getHealth()
    {
        try
        {
            final ClusterHealthResponse healthResponse = doGetHealth();
            if ( healthResponse.getStatus() != org.elasticsearch.cluster.health.ClusterHealthStatus.RED )
            {
                if ( !checkAllIndicesOpened() )
                {
                    return ClusterHealth.create().
                        status( ClusterHealthStatus.RED ).
                        errorMessage( "Closed indices" ).
                        build();
                }
            }
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

    private boolean checkAllIndicesOpened()
    {
        final ClusterStateRequest clusterStateRequest = Requests.clusterStateRequest().
            clear().
            metaData( true ).
            masterNodeTimeout( CLUSTER_HEALTH_TIMEOUT );
        final ClusterStateResponse clusterStateResponse = this.node.client().
            admin().
            cluster().
            state( clusterStateRequest ).
            actionGet();
        final UnmodifiableIterator<IndexMetaData> indiceIterator = clusterStateResponse.getState().
            getMetaData().
            getIndices().
            valuesIt();
        while ( indiceIterator.hasNext() )
        {
            final IndexMetaData indexMetaData = indiceIterator.next();
            if ( IndexMetaData.State.CLOSE == indexMetaData.getState() )
            {
                return false;
            }
        }
        return true;
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

    private synchronized void registerClient()
    {
        if ( this.clientServiceRegistration != null )
        {
            return;
        }

        LOG.info( "Cluster operational, register elasticsearch-client" );
        this.clientServiceRegistration = this.bundleContext.registerService( Client.class, this.node.client(), new Hashtable<>() );
    }

    private synchronized void unregisterClient()
    {
        if ( this.clientServiceRegistration == null )
        {
            return;
        }

        try
        {
            LOG.info( "Cluster not operational, unregister elasticsearch-client" );
            this.clientServiceRegistration.unregister();
        }
        finally
        {
            this.clientServiceRegistration = null;
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
}
