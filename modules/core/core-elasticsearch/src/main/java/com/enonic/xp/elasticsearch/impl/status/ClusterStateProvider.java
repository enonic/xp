package com.enonic.xp.elasticsearch.impl.status;

import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequest;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.cluster.node.DiscoveryNodes;

import com.google.common.collect.Lists;


public final class ClusterStateProvider
    extends ClusterInfoProvider<ClusterState>
{
    private String masterNodeId;

    public ClusterStateProvider( final Client client )
    {
        super( client );
    }

    @Override
    public ClusterState getInfo()
    {

        final ClusterState.Builder builder = ClusterState.create();
        ClusterStateResponse clusterStateResponse = null;
        NodesInfoResponse localNodeInfoResponse = null;
        try
        {
            clusterStateResponse = this.getClusterStateResponse();
            builder.clusterName( clusterStateResponse.getClusterName().value() );

            localNodeInfoResponse = this.getNodesResponse();
        }
        catch ( ElasticsearchException ex )
        {
            if ( clusterStateResponse == null )
            {
                return null;
            }
            builder.errorMessage( ex.getMessage() );
        }

        final org.elasticsearch.cluster.ClusterState clusterState = clusterStateResponse.getState();

        this.masterNodeId = clusterState.getNodes().getMasterNodeId();

        if ( localNodeInfoResponse != null )
        {
            final NodeInfo localNodeInfo = localNodeInfoResponse.getAt( 0 );
            final LocalNodeState localNodeState = this.getLocalNodeState( clusterState, localNodeInfo );
            builder.localNodeState( localNodeState );
        }

        List<MemberNodeState> memberNodeStates = this.getMembersState( clusterState.getNodes() );
        builder.addMemberNodeStates( memberNodeStates );

        return builder.build();
    }

    private LocalNodeState getLocalNodeState( org.elasticsearch.cluster.ClusterState clusterState, final NodeInfo localNodeInfo )
    {
        final String nodeId = localNodeInfo.getNode().getId();

        return (LocalNodeState) LocalNodeState.create().
            numberOfNodesSeen( clusterState.getNodes().size() ).
            id( nodeId ).
            hostName( localNodeInfo.getNode().getHostName() ).
            master( nodeId.equals( this.masterNodeId ) ).
            build();
    }

    private List<MemberNodeState> getMembersState( DiscoveryNodes members )
    {

        final List<MemberNodeState> results = Lists.newArrayList();

        for ( DiscoveryNode node : members )
        {
            final MemberNodeState memberNodeState = (MemberNodeState) MemberNodeState.create().
                address( node.getAddress().toString() ).
                version( node.getVersion() ).
                id( node.id() ).
                hostName( node.getHostName() ).
                master( node.getId().equals( this.masterNodeId ) ).build();

            results.add( memberNodeState );
        }

        return results;
    }


    private ClusterStateResponse getClusterStateResponse()
    {
        final ClusterStateRequest clusterStateRequest = Requests.clusterStateRequest().
            listenerThreaded( false ).
            blocks( false ).
            routingTable( false ).
            indices( "" ).
            metaData( false ).
            masterNodeTimeout( CLUSTER_HEALTH_TIMEOUT );

        return client.admin().cluster().state( clusterStateRequest ).actionGet();
    }

    private NodesInfoResponse getNodesResponse( String... nodeIds )
    {
        final NodesInfoRequest req =
            ( nodeIds != null && nodeIds.length > 0 ) ? new NodesInfoRequest( nodeIds ) : new NodesInfoRequest().all();
        return this.client.admin().cluster().nodesInfo( req ).actionGet();
    }
}
