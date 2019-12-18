package com.enonic.xp.elasticsearch.impl.status.cluster;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = ClusterStateProvider.class)
public final class ClusterStateProvider
    implements ClusterInfoProvider<ClusterState>
{
    private RestHighLevelClient client;

    @Override
    public ClusterState getInfo()
    {
        final ClusterState.Builder builder = ClusterState.create();

        try
        {
            //doPopulateClusterState( builder );
        }
        catch ( ElasticsearchException ex )
        {
            builder.errorMessage( ex.getClass().getSimpleName() + "[" + ex.getMessage() + "]" ).
                build();
        }

        return builder.build();
    }

    private void doPopulateClusterState( final ClusterState.Builder builder )
    {
        ClusterStateResponse clusterStateResponse = this.getClusterStateResponse();

        builder.clusterName( clusterStateResponse.getClusterName().value() );

        final org.elasticsearch.cluster.ClusterState clusterState = clusterStateResponse.getState();

        final DiscoveryNodes clusterMembers = clusterState.getNodes();

        List<MemberNodeState> memberNodeStates = this.getMembersState( clusterMembers );
        builder.addMemberNodeStates( memberNodeStates );
    }

    private List<MemberNodeState> getMembersState( DiscoveryNodes members )
    {
        final List<MemberNodeState> results = new ArrayList<>();

        for ( DiscoveryNode node : members )
        {
            final MemberNodeState memberNodeState = MemberNodeState.create().
                address( node.getAddress().toString() ).
                hostName( node.getHostName() ).
                id( node.getId() ).
                hostName( node.getHostName() ).
                version( node.getVersion().toString() ).
                name( node.getName() ).
                master( node.getId().equals( members.getMasterNodeId() ) ).
                isDataNode( node.isDataNode() ).
                isClientNode( false ).
                build();

            results.add( memberNodeState );
        }

        return results;
    }

    private ClusterStateResponse getClusterStateResponse()
    {
        final ClusterStateRequest clusterStateRequest = Requests.clusterStateRequest().
            clear().
            nodes( true ).
            indices( "" ).
            masterNodeTimeout( CLUSTER_HEALTH_TIMEOUT );

        //return restHighLevelClient.state( clusterStateRequest ).actionGet();
        return null;
    }

    @Reference
    public void setClient( RestHighLevelClient client )
    {
        this.client = client;
    }
}
