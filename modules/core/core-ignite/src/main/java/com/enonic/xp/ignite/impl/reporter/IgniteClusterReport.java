package com.enonic.xp.ignite.impl.reporter;

import java.util.Collection;

import org.apache.ignite.DataRegionMetrics;
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
        json.set( "nodes", createClusterNodesNode( this.cluster.nodes() ) );
        json.set( "local", createClusterNodeNode( this.cluster.localNode() ) );
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

    private JsonNode createClusterNodeNode( final ClusterNode n )
    {
        ObjectNode clusterNode = JsonNodeFactory.instance.objectNode();
        clusterNode.put( "id", n.id().toString() );
        clusterNode.put( "local", n.isLocal() );
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
        private Collection<DataRegionMetrics> regionMetrics;

        private IgniteCluster cluster;

        private Builder()
        {
        }

        Builder regionMetrics( final Collection<DataRegionMetrics> val )
        {
            regionMetrics = val;
            return this;
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
