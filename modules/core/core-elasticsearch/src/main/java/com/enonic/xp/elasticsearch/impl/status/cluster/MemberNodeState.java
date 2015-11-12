package com.enonic.xp.elasticsearch.impl.status.cluster;

import org.elasticsearch.Version;

import com.fasterxml.jackson.databind.node.ObjectNode;

public final class MemberNodeState
    extends NodeState
{
    private String address;

    private Version version;

    private MemberNodeState( Builder builder )
    {
        super( builder );
        this.address = builder.address;
        this.version = builder.version;
    }

    public ObjectNode toJson()
    {
        final ObjectNode json = super.toJson();
        json.put( "address", this.address );
        json.put( "version", this.version.toString() );
        return json;
    }

    public static MemberNodeState.Builder create()
    {
        return new Builder();
    }


    public static class Builder
        extends NodeState.Builder
    {
        private String address;

        private Version version;

        private Builder()
        {
        }

        public Builder address( final String address )
        {
            this.address = address;
            return this;
        }

        public Builder version( final Version version )
        {
            this.version = version;
            return this;
        }

        public MemberNodeState build()
        {
            return new MemberNodeState( this );
        }
    }
}
