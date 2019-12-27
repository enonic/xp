package com.enonic.xp.elasticsearch.impl.status.cluster;

import java.io.IOException;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.elasticsearch.client.impl.EsClient;

@Component(service = ClusterHealthProvider.class)
public final class ClusterHealthProvider
    implements ClusterInfoProvider<ClusterHealth>
{
    private EsClient client;

    @Override
    public ClusterHealth getInfo()
    {
        final ClusterHealth.Builder builder = ClusterHealth.create();
        try
        {
            ClusterHealthResponse clusterHealthResponse = this.getClusterHealthResponse();
            builder.clusterHealthStatus( clusterHealthResponse.getStatus().toString() );
        }
        catch ( Exception ex )
        {
            builder.errorMessage( ex.getClass().getSimpleName() + "[" + ex.getMessage() + "]" );
        }

        return builder.build();
    }

    private ClusterHealthResponse getClusterHealthResponse()
        throws IOException
    {
        String[] indices = new String[]{};

        final ClusterHealthRequest request = new ClusterHealthRequest().
            timeout( CLUSTER_HEALTH_TIMEOUT ).
            indices( indices );

        return client.clusterHealth( request );
    }

    @Reference
    public void setClient( EsClient client )
    {
        this.client = client;
    }
}
