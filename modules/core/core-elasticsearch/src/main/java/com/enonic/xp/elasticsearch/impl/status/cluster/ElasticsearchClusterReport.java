package com.enonic.xp.elasticsearch.impl.status.cluster;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static com.google.common.base.Strings.isNullOrEmpty;

final class ElasticsearchClusterReport
{
    private final ClusterState clusterState;

    private final ClusterHealth clusterHealth;

    private ElasticsearchClusterReport( final Builder builder )
    {
        this.clusterState = builder.clusterState;
        this.clusterHealth = builder.clusterHealth;
    }

    ObjectNode toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        final ArrayNode errorMessages = JsonNodeFactory.instance.arrayNode();

        if ( clusterState != null )
        {
            if ( clusterState.getClusterName() != null )
            {
                json.put( "name", clusterState.getClusterName() );
            }
            if ( clusterState.getLocalNodeState() != null )
            {
                json.set( "localNode", clusterState.getLocalNodeState().toJson() );
            }
            if ( clusterState.getMemberNodeStateList() != null && !clusterState.getMemberNodeStateList().isEmpty() )
            {
                final ArrayNode nodesJson = json.putArray( "members" );
                for ( final MemberNodeState node : clusterState.getMemberNodeStateList() )
                {
                    nodesJson.add( node.toJson() );
                }
            }
            if ( !isNullOrEmpty( clusterState.getErrorMessage() ) )
            {
                errorMessages.add( clusterState.getErrorMessage() );
            }
        }
        else
        {
            errorMessages.add( "not able to get cluster state" );
        }

        if ( clusterHealth != null )
        {
            if ( !isNullOrEmpty( clusterHealth.getClusterHealthStatus() ) )
            {
                json.put( "state", clusterHealth.getClusterHealthStatus() );
            }
            if ( !isNullOrEmpty( clusterHealth.getErrorMessage() ) )
            {
                errorMessages.add( clusterHealth.getErrorMessage() );
            }
        }
        else
        {
            errorMessages.add( "not able to get cluster health info" );
        }

        if ( errorMessages.size() > 0 )
        {
            json.set( "errorMessages", errorMessages );
        }
        return json;
    }

    static ElasticsearchClusterReport.Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private ClusterState clusterState;

        private ClusterHealth clusterHealth;

        private Builder()
        {
        }

        Builder clusterState( final ClusterState clusterState )
        {
            this.clusterState = clusterState;
            return this;
        }

        Builder clusterHealth( final ClusterHealth clusterHealth )
        {
            this.clusterHealth = clusterHealth;
            return this;
        }

        ElasticsearchClusterReport build()
        {
            return new ElasticsearchClusterReport( this );
        }


    }


}
