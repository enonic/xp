package com.enonic.xp.ignite.impl.reporter;

import java.util.Collection;

import org.apache.ignite.IgniteCluster;
import org.apache.ignite.cluster.ClusterNode;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


class IgniteClusterReport
{
    private final IgniteCluster cluster;

    private IgniteClusterReport( final Builder builder )
    {
        cluster = builder.cluster;
    }

    JsonNode toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.set( "members", createClusterNodesNode( this.cluster.nodes() ) );
        json.set( "localNode", createLocalNode( this.cluster.localNode(), this.cluster.nodes().size() ) );
        return json;
    }

    private ArrayNode createClusterNodesNode( final Collection<ClusterNode> nodes )
    {
        final ArrayNode clusterNodes = JsonNodeFactory.instance.arrayNode();

        nodes.forEach( n -> {
            clusterNodes.add( createClusterNodeNode( n ) );
        } );

        return clusterNodes;
    }

    private JsonNode createLocalNode( final ClusterNode n, final int size )
    {
        ObjectNode clusterNode = JsonNodeFactory.instance.objectNode();
        clusterNode.put( "id", n.id().toString() );
        clusterNode.put( "numberOfNodesSeen", size );
        return clusterNode;
    }

    private JsonNode createClusterNodeNode( final ClusterNode n )
    {
        ObjectNode clusterNode = JsonNodeFactory.instance.objectNode();
        clusterNode.put( "id", n.id().toString() );
        clusterNode.put( "name", n.consistentId().toString() );
        clusterNode.put( "local", n.isLocal() );
        clusterNode.put( "isClient", n.isClient() );
        clusterNode.put( "isDeamon", n.isDaemon() );
        clusterNode.put( "order", n.order() );
        clusterNode.set( "addresses", createStringArrayNode( n.addresses() ) );
        clusterNode.set( "hostNames", createStringArrayNode( n.hostNames() ) );
        return clusterNode;
    }

    private ArrayNode createStringArrayNode( final Collection<String> collection )
    {
        final ArrayNode stringArrayNode = JsonNodeFactory.instance.arrayNode();
        collection.forEach( stringArrayNode::add );
        return stringArrayNode;
    }

    static Builder create()
    {
        return new Builder();
    }


    static final class Builder
    {
        private IgniteCluster cluster;

        private Builder()
        {
        }

        Builder cluster( final IgniteCluster val )
        {
            cluster = val;
            return this;
        }

        IgniteClusterReport build()
        {
            return new IgniteClusterReport( this );
        }
    }
}
