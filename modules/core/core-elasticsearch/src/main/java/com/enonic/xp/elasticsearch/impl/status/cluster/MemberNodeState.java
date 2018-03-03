package com.enonic.xp.elasticsearch.impl.status.cluster;

import com.fasterxml.jackson.databind.node.ObjectNode;

final class MemberNodeState
    extends NodeState
{
    private String address;

    private Boolean isDataNode;

    private Boolean isClientNode;

    private MemberNodeState( Builder builder )
    {
        super( builder );
        this.address = builder.address;
        this.isDataNode = builder.isDataNode;
        this.isClientNode = builder.isClientNode;
    }

    ObjectNode toJson()
    {
        final ObjectNode json = super.toJson();
        json.put( "address", this.address );
        json.put( "isDataNode", this.isDataNode );
        json.put( "isClientNode", this.isClientNode );
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

        private Boolean isDataNode;

        private Boolean isClientNode;

        private Builder()
        {
        }

        Builder address( final String address )
        {
            this.address = address;
            return this;
        }

        Builder isDataNode( final Boolean isDataNode )
        {
            this.isDataNode = isDataNode;
            return this;
        }

        Builder isClientNode( final Boolean isClientNode )
        {
            this.isClientNode = isClientNode;
            return this;
        }

        MemberNodeState build()
        {
            return new MemberNodeState( this );
        }
    }
}
