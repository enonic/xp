package com.enonic.xp.elasticsearch.impl.status;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequestBuilder;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.Client;


public final class ClusterHealthProvider
    extends ClusterInfoProvider<ClusterHealth>
{

    public ClusterHealthProvider( final Client client )
    {
        super( client );
    }

    @Override
    public ClusterHealth getInfo()
    {
        final ClusterHealth.Builder builder = ClusterHealth.create();
        ClusterHealthResponse clusterHealthResponse = null;
        try
        {
            clusterHealthResponse = this.getClusterHealthResponse();
        }
        catch ( ElasticsearchException ex )
        {
            builder.errorMessage( ex.getMessage() );
            return builder.build();
        }

        if ( clusterHealthResponse == null )
        {
            return null;
        }

        return builder.clusterHealthStatus( clusterHealthResponse.getStatus().toString() ).
            build();
    }

    private ClusterHealthResponse getClusterHealthResponse()
    {
        String[] indices = new String[]{};

        final ClusterHealthRequest request = new ClusterHealthRequestBuilder( this.client.admin().cluster() ).
            setTimeout( CLUSTER_HEALTH_TIMEOUT ).
            setIndices( indices ).
            request();

        return client.admin().cluster().health( request ).actionGet();
    }
}
