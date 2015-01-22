package com.enonic.wem.api.node;

import java.util.Set;

import com.google.common.collect.Sets;

public class ResolveSyncWorkResult
{
    private final NodeIds publish;

    private final NodeIds delete;

    private final NodeIds conflict;

    private ResolveSyncWorkResult( Builder builder )
    {
        this.publish = NodeIds.from( builder.publish );
        this.delete = NodeIds.from( builder.delete );
        this.conflict = NodeIds.from( builder.conflict );
    }


    public NodeIds getPublish()
    {
        return publish;
    }

    public NodeIds getDelete()
    {
        return delete;
    }

    public NodeIds getConflict()
    {
        return conflict;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private Set<NodeId> publish = Sets.newHashSet();

        private Set<NodeId> delete = Sets.newHashSet();

        private Set<NodeId> conflict = Sets.newHashSet();

        private Builder()
        {
        }

        public Builder publish( final NodeId publish )
        {
            this.publish.add( publish );
            return this;
        }

        public Builder delete( final NodeId delete )
        {
            this.delete.add( delete );
            return this;
        }

        public Builder conflict( final NodeId nodeId )
        {
            this.conflict.add( nodeId );
            return this;
        }

        public ResolveSyncWorkResult build()
        {
            return new ResolveSyncWorkResult( this );
        }
    }
}
