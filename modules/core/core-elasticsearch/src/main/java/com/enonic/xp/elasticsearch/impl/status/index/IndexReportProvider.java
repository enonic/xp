package com.enonic.xp.elasticsearch.impl.status.index;

import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequestBuilder;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.admin.indices.stats.ShardStats;
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
            final IndicesStatsResponse indicesStatsResponse = new IndicesStatsRequestBuilder( adminClient.indices() ).
                all().
                get( TIMEOUT );

            final ShardStats[] shards = indicesStatsResponse.getShards();
            for ( ShardStats shardStats : shards )
            {
                final ShardRouting shardRouting = shardStats.getShardRouting();

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
