package com.enonic.xp.ignite.impl.reporter;

import java.util.Collection;

import org.apache.ignite.DataRegionMetrics;
import org.apache.ignite.DataStorageMetrics;
import org.apache.ignite.IgniteCluster;
import org.apache.ignite.cluster.ClusterNode;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


class IgniteClusterReport
{

    private final Collection<DataRegionMetrics> regionMetrics;

    private final IgniteCluster cluster;

    private final DataStorageMetrics storageMetrics;

    private IgniteClusterReport( final Builder builder )
    {
        regionMetrics = builder.regionMetrics;
        cluster = builder.cluster;
        storageMetrics = builder.storageMetrics;
    }

    JsonNode toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();

        json.set( "nodes", createClusterNodesNode( this.cluster.nodes() ) );
        json.set( "regions", createRegionMetrics() );

        return json;
    }

    private ArrayNode createRegionMetrics()
    {
        final ArrayNode regionsNode = JsonNodeFactory.instance.arrayNode();

        this.regionMetrics.forEach( m -> {
            ObjectNode regionNode = JsonNodeFactory.instance.objectNode();
            regionNode.put( "name", m.getName() );
            regionNode.put( "pages", m.getPhysicalMemoryPages() );
            regionNode.put( "allocatedPages", m.getTotalAllocatedPages() );
            regionNode.put( "evictionRate", m.getEvictionRate() );

            regionsNode.add( regionNode );
        } );

        return regionsNode;
    }

    private ArrayNode createClusterNodesNode( final Collection<ClusterNode> nodes )
    {
        final ArrayNode clusterNodes = JsonNodeFactory.instance.arrayNode();

        nodes.forEach( n -> {
            ObjectNode clusterNode = JsonNodeFactory.instance.objectNode();
            clusterNode.put( "id", n.id().toString() );
            clusterNode.put( "local", n.isLocal() );
            clusterNode.set( "addresses", createStringArrayNode( n.addresses() ) );
            clusterNode.set( "hostNames", createStringArrayNode( n.hostNames() ) );
            clusterNodes.add( clusterNode );
        } );

        return clusterNodes;
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

        private DataStorageMetrics storageMetrics;

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

        Builder storageMetrics( final DataStorageMetrics val )
        {
            storageMetrics = val;
            return this;
        }

        IgniteClusterReport build()
        {
            return new IgniteClusterReport( this );
        }
    }
}
