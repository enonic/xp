package com.enonic.xp.elasticsearch.impl.status.cluster;

import com.fasterxml.jackson.databind.node.ObjectNode;

public final class MemberNodeState
    extends NodeState
{
    private String address;

    private final String name;

    private MemberNodeState( Builder builder )
    {
        super( builder );
        this.address = builder.address;
        this.name = builder.name;
    }

    public ObjectNode toJson()
    {
        final ObjectNode json = super.toJson();
        json.put( "address", this.address );
        json.put( "name", this.name );
        return json;
    }

    public static MemberNodeState.Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends NodeState.Builder<Builder>
    {
        private String address;

        private String name;

        private Builder()
        {
        }

        public Builder address( final String address )
        {
            this.address = address;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public MemberNodeState build()
        {
            return new MemberNodeState( this );
        }
    }
}
