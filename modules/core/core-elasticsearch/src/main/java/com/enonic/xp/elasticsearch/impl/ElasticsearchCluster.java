package com.enonic.xp.elasticsearch.impl;

import java.util.stream.StreamSupport;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.client.Requests;
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

@Component(immediate = true)
public final class ElasticsearchCluster
    implements Cluster
{
    private static final String CLUSTER_HEALTH_TIMEOUT = "5s";

    private final ClusterId id = ClusterId.from( "elasticsearch" );

    private final ClusterAdminClient clusterAdminClient;


    @Activate
    public ElasticsearchCluster( @Reference final ClusterAdminClient clusterAdminClient )
    {
        this.clusterAdminClient = clusterAdminClient;
    }

    @Override
    public ClusterId getId()
    {
        return id;
    }

    @Override
    public ClusterHealth getHealth()
    {
        try
        {
            final ClusterHealthRequest clusterHealthRequest = new ClusterHealthRequest().timeout( CLUSTER_HEALTH_TIMEOUT );
            final ClusterHealthResponse healthResponse = clusterAdminClient.health( clusterHealthRequest ).actionGet();
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

            return StreamSupport.stream( response.getState().getNodes().spliterator(), false )
                .map( n -> ClusterNode.from( n.getName() ) )
                .collect( ClusterNodes.collector() );
        }
        catch ( Exception e )
        {
            return ClusterNodes.create().build();
        }
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