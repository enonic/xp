package com.enonic.xp.elasticsearch.impl.status;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class ClusterReport
{
    private final ClusterState clusterState;

    private final ClusterHealth clusterHealth;

    private ClusterReport( final Builder builder )
    {
        this.clusterState = builder.clusterState;
        this.clusterHealth = builder.clusterHealth;
    }

    public ObjectNode toJson()
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
            final ArrayNode nodesJson = json.putArray( "members" );
            for ( final MemberNodeState node : clusterState.getMemberNodeStateList() )
            {
                nodesJson.add( node.toJson() );
            }

            if ( StringUtils.isNotEmpty( clusterState.getErrorMessage() ) )
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
            if ( StringUtils.isNotEmpty( clusterHealth.getClusterHealthStatus() ) )
            {
                json.put( "state", clusterHealth.getClusterHealthStatus() );
            }
            if ( StringUtils.isNotEmpty( clusterHealth.getErrorMessage() ) )
            {
                errorMessages.add( clusterHealth.getErrorMessage() );
            }
        }
        else
        {
            errorMessages.add( "not able to get cluster health info" );
        }

        json.set( "errorMessages", errorMessages );
        return json;
    }

    public static ClusterReport.Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ClusterState clusterState;

        private ClusterHealth clusterHealth;

        private Builder()
        {
        }

        public Builder clusterState( final ClusterState clusterState )
        {
            this.clusterState = clusterState;
            return this;
        }

        public Builder clusterHealth( final ClusterHealth clusterHealth )
        {
            this.clusterHealth = clusterHealth;
            return this;
        }

        public ClusterReport build()
        {
            return new ClusterReport( this );
        }


    }


}
