package com.enonic.xp.elasticsearch.impl.status.cluster;

import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Lists;

@Component(service = ClusterStateProvider.class)
public final class ClusterStateProvider
    implements ClusterInfoProvider<ClusterState>
{
    private ClusterAdminClient clusterAdminClient;

    // Needed to workaround bug in cluster state report
    // where localNodeId = null for non-master nodes
    private ClusterService clusterService;

    @Override
    public ClusterState getInfo()
    {
        final ClusterState.Builder builder = ClusterState.create();

        try
        {
            doPopulateClusterState( builder );
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

        // This should be fetched from custerState.getLocalNodeId() instead
        // when es-bug is fixed
        final DiscoveryNode localNode = clusterService.localNode();

        builder.clusterName( clusterStateResponse.getClusterName().value() );

        final org.elasticsearch.cluster.ClusterState clusterState = clusterStateResponse.getState();

        final DiscoveryNodes clusterMembers = clusterState.getNodes();

        builder.localNodeState( createLocalNodeState( clusterMembers, localNode ) );
        List<MemberNodeState> memberNodeStates = this.getMembersState( clusterMembers );
        builder.addMemberNodeStates( memberNodeStates );
    }

    private LocalNodeState createLocalNodeState( final DiscoveryNodes clusterMembers, final DiscoveryNode localNode )
    {
        return LocalNodeState.create().
            numberOfNodesSeen( clusterMembers.size() ).
            id( localNode.id() ).
            hostName( localNode.getHostName() ).
            master( localNode.id().equals( clusterMembers.masterNodeId() ) ).
            version( localNode.getVersion().toString() ).
            build();
    }

    private List<MemberNodeState> getMembersState( DiscoveryNodes members )
    {
        final List<MemberNodeState> results = Lists.newArrayList();

        for ( DiscoveryNode node : members )
        {
            final MemberNodeState memberNodeState = MemberNodeState.create().
                address( node.getAddress().toString() ).
                hostName( node.getHostName() ).
                id( node.id() ).
                hostName( node.getHostName() ).
                version( node.getVersion().toString() ).
                name( node.getName() ).
                master( node.getId().equals( members.getMasterNodeId() ) ).
                isDataNode( node.isDataNode() ).
                isClientNode( node.isClientNode() ).
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

        return clusterAdminClient.state( clusterStateRequest ).actionGet();
    }

    @Reference
    public void setClusterAdminClient( ClusterAdminClient clusterAdminClient )
    {
        this.clusterAdminClient = clusterAdminClient;
    }

    @Reference
    public void setClusterService( final ClusterService clusterService )
    {
        this.clusterService = clusterService;
    }
}
