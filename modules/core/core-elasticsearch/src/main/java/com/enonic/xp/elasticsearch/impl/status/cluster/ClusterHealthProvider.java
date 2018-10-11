package com.enonic.xp.elasticsearch.impl.status.cluster;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthAction;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequestBuilder;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.ClusterAdminClient;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = ClusterHealthProvider.class)
public final class ClusterHealthProvider
    implements ClusterInfoProvider<ClusterHealth>
{
    private ClusterAdminClient clusterAdminClient;

    @Override
    public ClusterHealth getInfo()
    {
        final ClusterHealth.Builder builder = ClusterHealth.create();
        try
        {
            ClusterHealthResponse clusterHealthResponse = this.getClusterHealthResponse();
            builder.clusterHealthStatus( clusterHealthResponse.getStatus().toString() );
        }
        catch ( ElasticsearchException ex )
        {
            builder.errorMessage( ex.getClass().getSimpleName() + "[" + ex.getMessage() + "]" );
        }

        return builder.build();
    }

    private ClusterHealthResponse getClusterHealthResponse()
    {
        String[] indices = new String[]{};

        final ClusterHealthRequest request = new ClusterHealthRequestBuilder( clusterAdminClient, ClusterHealthAction.INSTANCE ).
            setTimeout( CLUSTER_HEALTH_TIMEOUT ).
            setIndices( indices ).
            request();

        return clusterAdminClient.health( request ).actionGet();
    }

    @Reference
    public void setClusterAdminClient( ClusterAdminClient clusterAdminClient )
    {
        this.clusterAdminClient = clusterAdminClient;
    }
}
