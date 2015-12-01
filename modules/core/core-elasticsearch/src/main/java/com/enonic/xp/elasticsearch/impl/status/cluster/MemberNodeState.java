package com.enonic.xp.elasticsearch.impl.status.cluster;

import com.fasterxml.jackson.databind.node.ObjectNode;

public final class MemberNodeState
    extends NodeState
{
    private String address;

    private MemberNodeState( Builder builder )
    {
        super( builder );
        this.address = builder.address;
    }

    public ObjectNode toJson()
    {
        final ObjectNode json = super.toJson();
        json.put( "address", this.address );
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

        private Builder()
        {
        }

        public Builder address( final String address )
        {
            this.address = address;
            return this;
        }

        public MemberNodeState build()
        {
            return new MemberNodeState( this );
        }
    }
}
