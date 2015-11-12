package com.enonic.xp.elasticsearch.impl.status;

import com.fasterxml.jackson.databind.node.ObjectNode;

public final class LocalNodeState
    extends NodeState
{
    private Integer numberOfNodesSeen;

    private LocalNodeState( Builder builder )
    {
        super( builder );
        this.numberOfNodesSeen = builder.numberOfNodesSeen;
    }

    public ObjectNode toJson()
    {
        final ObjectNode json = super.toJson();
        json.put( "numberOfNodesSeen", this.numberOfNodesSeen );
        return json;
    }

    public static LocalNodeState.Builder create()
    {
        return new Builder();
    }


    public static class Builder
        extends NodeState.Builder
    {
        private Integer numberOfNodesSeen;

        private Builder()
        {
        }

        public Builder numberOfNodesSeen( final Integer numberOfNodesSeen )
        {
            this.numberOfNodesSeen = numberOfNodesSeen;
            return this;
        }

        public LocalNodeState build()
        {
            return new LocalNodeState( this );
        }
    }

}
