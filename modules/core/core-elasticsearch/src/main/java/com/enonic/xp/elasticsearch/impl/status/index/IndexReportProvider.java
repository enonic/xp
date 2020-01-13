package com.enonic.xp.elasticsearch.impl.status.index;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.elasticsearch.client.impl.EsClient;
import com.enonic.xp.elasticsearch.client.impl.cluster.state.GetClusterStateRequest;
import com.enonic.xp.elasticsearch.client.impl.cluster.state.GetClusterStateResponse;
import com.enonic.xp.elasticsearch.client.impl.cluster.state.IndexShardRoutingState;
import com.enonic.xp.elasticsearch.client.impl.cluster.state.IndexShardRoutingTable;
import com.enonic.xp.elasticsearch.client.impl.cluster.state.RoutingTable;
import com.enonic.xp.elasticsearch.client.impl.nodes.Node;

@Component(service = IndexReportProvider.class)
public class IndexReportProvider
{
    private static final String TIMEOUT = "3s";

    private EsClient client;

    public IndexReport getInfo()
    {
        final IndexReport.Builder builder = IndexReport.create();
        try
        {
            final GetClusterStateResponse clusterStateResponse = getClusterState();

            setShardSummary( builder, clusterStateResponse );

            setShardInfo( builder, clusterStateResponse );
        }
        catch ( Exception e )
        {
            builder.errorMessage( e.getClass().getSimpleName() + "[" + e.getMessage() + "]" );
        }
        return builder.build();
    }

    private void setShardSummary( final IndexReport.Builder builder, final GetClusterStateResponse clusterStateResponse )
    {
        final ShardSummary.Builder summaryBuilder = ShardSummary.create();

        final RoutingTable routingTable = clusterStateResponse.getRoutingTable();

        summaryBuilder.started( routingTable.shardsWithState( IndexShardRoutingState.STARTED ).size() );
        summaryBuilder.unassigned( routingTable.shardsWithState( IndexShardRoutingState.UNASSIGNED ).size() );
        summaryBuilder.initializing( routingTable.shardsWithState( IndexShardRoutingState.INITIALIZING ).size() );
        summaryBuilder.relocating( routingTable.shardsWithState( IndexShardRoutingState.RELOCATING ).size() );

        builder.shardSummart( summaryBuilder.build() );
    }

    private void setShardInfo( final IndexReport.Builder builder, final GetClusterStateResponse clusterStateResponse )
    {
        final Map<String, Node> nodes = client.nodes().getNodes().stream().collect( Collectors.toMap( Node::getId, Function.identity() ) );

        final ShardInfo.Builder shardInfoBuilder = ShardInfo.create();

        final RoutingTable routingTable = clusterStateResponse.getRoutingTable();

        shardInfoBuilder.started( create( routingTable.shardsWithState( IndexShardRoutingState.STARTED ), nodes ) );
        shardInfoBuilder.unassigned( create( routingTable.shardsWithState( IndexShardRoutingState.UNASSIGNED ), nodes ) );
        shardInfoBuilder.initializing( create( routingTable.shardsWithState( IndexShardRoutingState.INITIALIZING ), nodes ) );
        shardInfoBuilder.relocating( create( routingTable.shardsWithState( IndexShardRoutingState.RELOCATING ), nodes ) );

        builder.shardInfo( shardInfoBuilder.build() );
    }

    private List<ShardDetails> create( final List<IndexShardRoutingTable> shardRoutingList, final Map<String, Node> nodes )
    {
        final List<ShardDetails> list = new ArrayList<>();

        shardRoutingList.forEach( ( routing ) -> list.add( ShardDetails.create().
            id( routing.getIndex() + "(" + routing.getShard() + ")" ).
            nodeId( routing.getNodeId() ).
            primary( routing.isPrimary() ).
            relocatingNode( routing.getRelocatingNodeId() ).
            nodeAddress( getAddress( nodes, routing ) ).
            build() ) );

        return list;
    }

    private String getAddress( final Map<String, Node> nodes, final IndexShardRoutingTable indexShardRouting )
    {
        final Node node = nodes.get( indexShardRouting.getNodeId() );

        if ( node == null )
        {
            return "UNKNOWN";
        }

        final String hostAddress = node.getHostAddress();

        return hostAddress != null ? hostAddress : "UNKNOWN";
    }

    private GetClusterStateResponse getClusterState()
    {
        return client.clusterState( new GetClusterStateRequest( TIMEOUT ) );
    }

    @Reference
    public void setClient( EsClient client )
    {
        this.client = client;
    }
}
