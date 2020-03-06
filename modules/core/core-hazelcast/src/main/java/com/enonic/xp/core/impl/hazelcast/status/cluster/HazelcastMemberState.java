package com.enonic.xp.core.impl.hazelcast.status.cluster;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

final class HazelcastMemberState
{
    private final String address;

    private final int port;

    private final String uuid;

    private final boolean lightMember;

    private final String version;

    private HazelcastMemberState( Builder builder )
    {
        this.uuid = builder.uuid;
        this.address = builder.address;
        this.port = builder.port;
        this.lightMember = builder.lightMember;
        this.version = builder.version;
    }

    ObjectNode toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "uuid", this.uuid );
        json.put( "address", this.address );
        json.put( "port", this.port );
        json.put( "lightMember", this.lightMember );
        json.put( "version", this.version );
        return json;
    }

    static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        String address;

        int port;

        String uuid;

        boolean lightMember;

        String version;

        private Builder()
        {
        }

        Builder address( final String address )
        {
            this.address = address;
            return this;
        }

        Builder port( final int port )
        {
            this.port = port;
            return this;
        }

        Builder uuid( final String uuid )
        {
            this.uuid = uuid;
            return this;
        }

        Builder lightMember( final boolean lightMember )
        {
            this.lightMember = lightMember;
            return this;
        }

        Builder version( final String version )
        {
            this.version = version;
            return this;
        }

        HazelcastMemberState build()
        {
            return new HazelcastMemberState( this );
        }
    }
}
