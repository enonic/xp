package com.enonic.xp.elasticsearch.impl.status.cluster;

import com.fasterxml.jackson.databind.node.ObjectNode;

final class LocalNodeState
    extends NodeState
{
    private final Integer numberOfNodesSeen;

    private LocalNodeState( Builder builder )
    {
        super( builder );
        this.numberOfNodesSeen = builder.numberOfNodesSeen;
    }

    ObjectNode toJson()
    {
        final ObjectNode json = super.toJson();
        json.put( "numberOfNodesSeen", this.numberOfNodesSeen );
        return json;
    }

    static LocalNodeState.Builder create()
    {
        return new Builder();
    }

    static class Builder
        extends NodeState.Builder<Builder>
    {
        private Integer numberOfNodesSeen;

        private Builder()
        {
        }

        Builder numberOfNodesSeen( final Integer numberOfNodesSeen )
        {
            this.numberOfNodesSeen = numberOfNodesSeen;
            return this;
        }

        LocalNodeState build()
        {
            return new LocalNodeState( this );
        }
    }

}
