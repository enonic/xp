package com.enonic.xp.elasticsearch.impl;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.Cluster;
import com.enonic.xp.cluster.ClusterHealth;
import com.enonic.xp.cluster.ClusterHealthStatus;
import com.enonic.xp.cluster.ClusterId;
import com.enonic.xp.cluster.ClusterNodes;
import com.enonic.xp.elasticsearch.client.impl.EsClient;
import com.enonic.xp.elasticsearch.client.impl.nodes.GetNodesResponse;

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
            final GetNodesResponse response = client.nodes();

            return ClusterNodesFactory.create( response.getNodes() );
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
