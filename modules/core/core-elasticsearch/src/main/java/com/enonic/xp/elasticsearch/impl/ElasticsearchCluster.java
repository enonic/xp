package com.enonic.xp.elasticsearch.impl;

import java.io.IOException;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.Client;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.Cluster;
import com.enonic.xp.cluster.ClusterHealth;
import com.enonic.xp.cluster.ClusterHealthStatus;
import com.enonic.xp.cluster.ClusterId;
import com.enonic.xp.cluster.ClusterNode;
import com.enonic.xp.cluster.ClusterNodes;
import com.enonic.xp.elasticsearch.client.impl.EsClient;

@Component(immediate = true)
public final class ElasticsearchCluster
    implements Cluster
{
    private static final Logger LOG = LoggerFactory.getLogger( ElasticsearchCluster.class );

    private static final String CLUSTER_HEALTH_TIMEOUT = "5s";

    private EsClient client;

    private final ClusterId id = ClusterId.from( "elasticsearch" );

    @Activate
    public ElasticsearchCluster( @Reference final EsClient client )
    {
        this.client = client;
    }

    @Override
    public ClusterId getId()
    {
        return id;
    }

    @Override
    public boolean isEnabled()
    {
        return true;
    }

    @Override
    public ClusterHealth getHealth()
    {
        try
        {
            final ClusterHealthResponse healthResponse = doGetHealth();

            return toClusterHealth( healthResponse );
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
            final ClusterStateRequest clusterStateRequest = Requests.clusterStateRequest().
                clear().
                nodes( true ).
                masterNodeTimeout( CLUSTER_HEALTH_TIMEOUT );

            final ClusterStateResponse response = clusterAdminClient.state( clusterStateRequest ).actionGet();

            final DiscoveryNodes members = response.getState().getNodes();
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
    }

    @Override
    public void disable()
    {
    }

    private ClusterHealthResponse doGetHealth()
        throws IOException
    {
        return client.clusterHealth( new ClusterHealthRequest().
            timeout( CLUSTER_HEALTH_TIMEOUT ).
            waitForYellowStatus() );
    }

    private ClusterHealth toClusterHealth( final ClusterHealthResponse healthResponse )
    {
        switch ( healthResponse.getStatus() )
        {
            case GREEN:
                return ClusterHealth.green();
            case YELLOW:
                return ClusterHealth.yellow();
            case RED:
                return ClusterHealth.create().
                    status( ClusterHealthStatus.RED ).
                    errorMessage( healthResponse.toString() ).
                    build();
            default:
                throw new IllegalStateException( "Unknown ES Health status " + healthResponse.getStatus() );
        }
    }
}
