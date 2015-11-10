package com.enonic.xp.elasticsearch.impl.status;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.node.DiscoveryNode;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class ClusterReport
{
    private final ClusterState clusterState;

    private final NodeInfo localNodeInfo;

    private final ClusterHealthResponse clusterHealthResponse;

    private String masterNodeId;

    private ClusterReport( final Builder builder )
    {
        this.clusterState = builder.clusterState;
        this.localNodeInfo = builder.localNodeInfo;
        this.clusterHealthResponse = builder.clusterHealthResponse;
    }

    public ObjectNode toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        if ( clusterState == null )
        {
            json.put( "error", "not able to get cluster state" );
            return json;
        }

        if ( localNodeInfo == null )
        {
            json.put( "error", "not able to get localNodeInfo" );
            return json;
        }

        this.masterNodeId = clusterState.getNodes().getMasterNodeId();

        json.put( "name", clusterState.getClusterName().value() );
        json.put( "state", clusterHealthResponse.getStatus().toString() );
        json.set( "localNode", getLocalNodeJson() );

        final ArrayNode nodesJson = json.putArray( "members" );
        for ( final DiscoveryNode node : clusterState.getNodes() )
        {
            nodesJson.add( getMemberNodeJson( node ) );
        }

        return json;
    }

    private ObjectNode getLocalNodeJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();

        final String nodeId = localNodeInfo.getNode().getId();
        json.put( "hostName", localNodeInfo.getHostname() );
        json.put( "id", nodeId );
        json.put( "isMaster", Boolean.toString( nodeId.equals( masterNodeId ) ) );
        json.put( "numberOfNodesSeen", clusterState.getNodes().size() );
        return json;
    }

    private ObjectNode getMemberNodeJson( final DiscoveryNode node )
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();

        final String nodeId = node.getId();
        json.put( "address", node.getAddress().toString() );
        json.put( "hostName", node.getHostName() );
        json.put( "id", nodeId );
        json.put( "isMaster", Boolean.toString( nodeId.equals( masterNodeId ) ) );
        json.put( "version", node.getVersion().toString() );
        return json;
    }

    public static ClusterReport.Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ClusterState clusterState;

        private NodeInfo localNodeInfo;

        private ClusterHealthResponse clusterHealthResponse;

        private Builder()
        {
        }

        public Builder clusterState( final ClusterState clusterState )
        {
            this.clusterState = clusterState;
            return this;
        }

        public Builder localNodeInfo( final NodeInfo localNodeInfo )
        {
            this.localNodeInfo = localNodeInfo;
            return this;
        }

        public Builder clusterHealthResponse( final ClusterHealthResponse response )
        {
            this.clusterHealthResponse = response;
            return this;
        }

        public ClusterReport build()
        {
            return new ClusterReport( this );
        }


    }


}
