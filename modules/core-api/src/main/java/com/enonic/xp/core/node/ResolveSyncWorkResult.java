package com.enonic.xp.core.node;

import java.util.Set;

import com.google.common.collect.Sets;

public class ResolveSyncWorkResult
{
    private final NodePublishRequests nodePublishRequests;

    private final NodeIds delete;

    private final NodeIds conflict;

    private ResolveSyncWorkResult( Builder builder )
    {
        this.nodePublishRequests = builder.nodePublishRequests;
        this.delete = NodeIds.from( builder.delete );
        this.conflict = NodeIds.from( builder.conflict );
    }

    public boolean hasConflicts()
    {
        return getConflict().isNotEmpty();
    }

    public boolean hasPublishOutsideSelection()
    {
        return this.nodePublishRequests.hasPublishOutsideSelection();
    }

    public NodePublishRequests getNodePublishRequests()
    {
        return nodePublishRequests;
    }

    public NodeIds getDelete()
    {
        return delete;
    }

    NodeIds getConflict()
    {
        return conflict;
    }


    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final NodePublishRequests nodePublishRequests = new NodePublishRequests();

        private final Set<NodeId> delete = Sets.newHashSet();

        private final Set<NodeId> conflict = Sets.newHashSet();

        private Builder()
        {
        }

        public Builder publishRequested( final NodeId nodeId )
        {
            this.nodePublishRequests.add( NodePublishRequest.requested( nodeId ) );
            return this;
        }

        public Builder publishParentFor( final NodeId nodeId, final NodeId parentOf )
        {
            this.nodePublishRequests.add( NodePublishRequest.parentFor( nodeId, parentOf ) );
            return this;
        }

        public Builder publishReferredFrom( final NodeId nodeId, final NodeId referredFrom )
        {
            this.nodePublishRequests.add( NodePublishRequest.referredFrom( nodeId, referredFrom ) );
            return this;
        }

        public Builder addDelete( final NodeId delete )
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
