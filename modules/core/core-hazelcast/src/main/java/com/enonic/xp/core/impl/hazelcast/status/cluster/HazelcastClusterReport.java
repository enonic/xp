package com.enonic.xp.core.impl.hazelcast.status.cluster;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

class HazelcastClusterReport
{
    private final String clusterState;

    private final long clusterTime;

    private final String clusterVersion;

    private final List<HazelcastMemberState> memberStates;

    HazelcastClusterReport( final Builder builder )
    {
        this.clusterState = builder.clusterState;
        this.clusterTime = builder.clusterTime;
        this.clusterVersion = builder.clusterVersion;
        this.memberStates = List.copyOf( builder.memberStates );
    }


    ObjectNode toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "clusterState", clusterState );
        json.put( "clusterTime", clusterTime );
        json.put( "clusterVersion", clusterVersion );
        final ArrayNode membersJson = json.putArray( "members" );
        memberStates.stream().map( HazelcastMemberState::toJson ).forEach( membersJson::add );

        return json;
    }

    static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        String clusterState;

        long clusterTime;

        String clusterVersion;

        List<HazelcastMemberState> memberStates = new ArrayList<>();

        HazelcastClusterReport build()
        {
            return new HazelcastClusterReport( this );
        }

        public Builder clusterState( final String clusterState )
        {
            this.clusterState = clusterState;
            return this;
        }

        public Builder clusterTime( final long clusterTime )
        {
            this.clusterTime = clusterTime;
            return this;
        }

        public Builder clusterVersion( final String clusterVersion )
        {
            this.clusterVersion = clusterVersion;
            return this;
        }

        public Builder addMember( final HazelcastMemberState memberState )
        {
            memberStates.add( memberState );
            return this;
        }
    }
}
