package com.enonic.xp.elasticsearch.impl.status.cluster;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.elasticsearch.client.impl.EsClient;
import com.enonic.xp.elasticsearch.client.impl.nodes.GetNodesResponse;
import com.enonic.xp.elasticsearch.client.impl.nodes.Node;

@Component(service = ClusterStateProvider.class)
public final class ClusterStateProvider
    implements ClusterInfoProvider<ClusterState>
{
    private EsClient client;

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
        final GetNodesResponse response = client.nodes();

        builder.clusterName( response.getClusterName() );

        List<MemberNodeState> memberNodeStates = this.getMembersState( response.getNodes() );
        builder.addMemberNodeStates( memberNodeStates );
    }

    private List<MemberNodeState> getMembersState( final List<Node> nodes )
    {
        final List<MemberNodeState> results = new ArrayList<>();

        for ( Node node : nodes )
        {
            final MemberNodeState memberNodeState = MemberNodeState.create().
                address( node.getAddress() ).
                id( node.getId() ).
                hostName( node.getHostName() ).
                version( node.getVersion() ).
                name( node.getName() ).
                master( false ).
                isDataNode( node.isDataNode() ).
                isClientNode( false ).
                build();

            results.add( memberNodeState );
        }

        return results;
    }

    @Reference
    public void setClient( EsClient client )
    {
        this.client = client;
    }
}
