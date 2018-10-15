package com.enonic.xp.elasticsearch.impl.status.index;

import java.util.List;

import org.elasticsearch.action.admin.cluster.state.ClusterStateAction;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequestBuilder;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.cluster.routing.RoutingTable;
import org.elasticsearch.cluster.routing.ShardRouting;
import org.elasticsearch.cluster.routing.ShardRoutingState;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Lists;

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
            final ClusterStateResponse clusterStateResponse = getClusterState();

            setShardSummary( builder, clusterStateResponse );

            setShardInfo( builder, clusterStateResponse );
        }
        catch ( Exception e )
        {
            builder.errorMessage( e.getClass().getSimpleName() + "[" + e.getMessage() + "]" );
        }
        return builder.build();
    }

    private void setShardSummary( final IndexReport.Builder builder, final ClusterStateResponse clusterStateResponse )
    {
        final ShardSummary.Builder summaryBuilder = ShardSummary.create();

        final RoutingTable routingTable = clusterStateResponse.getState().getRoutingTable();

        summaryBuilder.started( routingTable.shardsWithState( ShardRoutingState.STARTED ).size() );
        summaryBuilder.unassigned( routingTable.shardsWithState( ShardRoutingState.UNASSIGNED ).size() );
        summaryBuilder.initializing( routingTable.shardsWithState( ShardRoutingState.INITIALIZING ).size() );
        summaryBuilder.relocating( routingTable.shardsWithState( ShardRoutingState.RELOCATING ).size() );

        builder.shardSummart( summaryBuilder.build() );
    }

    private void setShardInfo( final IndexReport.Builder builder, final ClusterStateResponse clusterStateResponse )
    {
        final ShardInfo.Builder shardInfoBuilder = ShardInfo.create();

        final RoutingTable routingTable = clusterStateResponse.getState().getRoutingTable();

        final DiscoveryNodes nodes = clusterStateResponse.getState().getNodes();

        shardInfoBuilder.started( create( routingTable.shardsWithState( ShardRoutingState.STARTED ), nodes ) );
        shardInfoBuilder.unassigned( create( routingTable.shardsWithState( ShardRoutingState.UNASSIGNED ), nodes ) );
        shardInfoBuilder.initializing( create( routingTable.shardsWithState( ShardRoutingState.INITIALIZING ), nodes ) );
        shardInfoBuilder.relocating( create( routingTable.shardsWithState( ShardRoutingState.RELOCATING ), nodes ) );

        builder.shardInfo( shardInfoBuilder.build() );
    }


    private List<ShardDetails> create( final List<ShardRouting> shardRoutingList, final DiscoveryNodes discoveryNodes )
    {
        List<ShardDetails> list = Lists.newArrayList();

        shardRoutingList.forEach( ( routing ) -> list.add( ShardDetails.create().
            id( routing.index() + "(" + routing.getId() + ")" ).
            nodeId( routing.currentNodeId() ).
            nodeAddress( getAddress( discoveryNodes, routing ) ).
            primary( routing.primary() ).
            relocatingNode( routing.relocatingNodeId() ).
            build() ) );

        return list;
    }

    private String getAddress( final DiscoveryNodes discoveryNodes, final ShardRouting routing )
    {
        final DiscoveryNode node = discoveryNodes.get( routing.currentNodeId() );

        return node != null ? node.getHostAddress() : "UNKNOWN";
    }

    private ClusterStateResponse getClusterState()
    {
        return new ClusterStateRequestBuilder( adminClient.cluster(), ClusterStateAction.INSTANCE ).
            clear().
            setRoutingTable( true ).
            setNodes( true ).
            get( TIMEOUT );
    }

    @Reference
    public void setAdminClient( AdminClient adminClient )
    {
        this.adminClient = adminClient;
    }
}
