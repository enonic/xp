package com.enonic.xp.cluster.impl.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.cluster.ClusterState;
import com.enonic.xp.cluster.Clusters;

class ClusterManagerReport
{
    private final Clusters clusters;

    private final ClusterState clusterState;

    private ClusterManagerReport( final Builder builder )
    {
        clusters = builder.clusters;
        clusterState = builder.clusterState;
    }

    ObjectNode toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "state", clusterState.name() );
        json.set( "clusters", renderClusters() );
        return json;
    }

    private JsonNode renderClusters()
    {
        final ArrayNode clusterNodes = JsonNodeFactory.instance.arrayNode();

        this.clusters.forEach( c -> {
            ObjectNode clusterNode = JsonNodeFactory.instance.objectNode();
            clusterNode.put( "id", c.getId().toString() );
            clusterNode.put( "enabled", c.isEnabled() );
            clusterNode.put( "healthy", c.getHealth().isHealthy() );
            clusterNode.put( "numberOfNodesSeen", c.getNodes().getSize() );

            clusterNodes.add( clusterNode );
        } );

        return clusterNodes;
    }

    static Builder create()
    {
        return new Builder();
    }

    static final class Builder
    {
        private Clusters clusters;

        private ClusterState clusterState;

        private Builder()
        {
        }

        Builder clusters( final Clusters val )
        {
            clusters = val;
            return this;
        }

        Builder clusterState( final ClusterState val )
        {
            clusterState = val;
            return this;
        }

        ClusterManagerReport build()
        {
            return new ClusterManagerReport( this );
        }
    }
}
