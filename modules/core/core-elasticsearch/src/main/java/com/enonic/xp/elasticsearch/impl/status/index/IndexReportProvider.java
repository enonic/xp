package com.enonic.xp.elasticsearch.impl.status.index;

import java.util.List;

import org.elasticsearch.action.admin.cluster.state.ClusterStateRequestBuilder;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.cluster.routing.ShardRouting;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = IndexReportProvider.class)
public class IndexReportProvider
{
    private static final String TIMEOUT = "3s";

    private AdminClient adminClient;

    public IndexReport getInfo()
    {
        final IndexReport.Builder builder = IndexReport.create();
        try
        {
            final ClusterStateResponse clusterStateResponse = new ClusterStateRequestBuilder( adminClient.cluster() ).
                clear().
                setRoutingTable( true ).
                get( TIMEOUT );

            final List<ShardRouting> shardRoutings = clusterStateResponse.getState().
                getRoutingTable().
                allShards();

            for ( ShardRouting shardRouting : shardRoutings )
            {

                final ShardInfo shardInfo = ShardInfo.create().
                    index( shardRouting.index() ).
                    id( shardRouting.id() ).
                    primary( shardRouting.primary() ).
                    state( shardRouting.state().name() ).
                    node( shardRouting.currentNodeId() ).
                    build();

                builder.addShardInfo( shardInfo );
            }
        }
        catch ( Exception e )
        {
            builder.errorMessage( e.getClass().getSimpleName() + "[" + e.getMessage() + "]" );
        }
        return builder.build();
    }

    @Reference
    public void setAdminClient( AdminClient adminClient )
    {
        this.adminClient = adminClient;
    }
}
