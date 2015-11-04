package com.enonic.xp.elasticsearch.impl.status;

import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequest;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.status.StatusReporter;

@Component(immediate = true)
public final class ClusterReporter
    implements StatusReporter
{
    private Client client;

    @Override
    public String getName()
    {
        return "cluster";
    }

    @Override
    public ObjectNode getReport()
    {
        final ClusterStateResponse clusterStateResponse = getClusterStateInfo();

        final NodeInfo localNodeInfo = this.getNodesInfo( "_local" ).getAt( 0 );

        final ClusterReport clusterReport = ClusterReport.create().
            clusterState( clusterStateResponse.getState() ).
            localNodeInfo( localNodeInfo ).
            build();

        return clusterReport.toJson();
    }

    private ClusterStateResponse getClusterStateInfo()
    {
        final ClusterStateRequest clusterStateRequest = Requests.clusterStateRequest();
        clusterStateRequest.listenerThreaded( false );
        return client.admin().cluster().state( clusterStateRequest ).actionGet();
    }

    private NodesInfoResponse getNodesInfo( String... nodeIds )
    {
        final NodesInfoRequest req =
            ( nodeIds != null && nodeIds.length > 0 ) ? new NodesInfoRequest( nodeIds ) : new NodesInfoRequest().all();
        return this.client.admin().cluster().nodesInfo( req ).actionGet();
    }

    @Reference
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
