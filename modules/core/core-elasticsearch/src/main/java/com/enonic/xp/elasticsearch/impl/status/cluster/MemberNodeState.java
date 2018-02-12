package com.enonic.xp.elasticsearch.impl.status.cluster;

import com.fasterxml.jackson.databind.node.ObjectNode;

final class MemberNodeState
    extends NodeState
{
    private final String address;

    private final String name;

    private MemberNodeState( Builder builder )
    {
        super( builder );
        this.address = builder.address;
        this.name = builder.name;
    }

    ObjectNode toJson()
    {
        final ObjectNode json = super.toJson();
        json.put( "address", this.address );
        json.put( "name", this.name );
        return json;
    }

    static MemberNodeState.Builder create()
    {
        return new Builder();
    }

    static class Builder
        extends NodeState.Builder<Builder>
    {
        private String address;

        private String name;

        private Builder()
        {
        }

        Builder address( final String address )
        {
            this.address = address;
            return this;
        }

        Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        MemberNodeState build()
        {
            return new MemberNodeState( this );
        }
    }
}
