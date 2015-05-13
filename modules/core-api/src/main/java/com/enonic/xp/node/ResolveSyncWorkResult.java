package com.enonic.xp.node;

import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.Sets;

@Beta
public class ResolveSyncWorkResult
{
    private final NodePublishRequests nodePublishRequests;

    private final NodePublishRequests nodeDeleteRequests;

    private final NodeIds delete;

    private final NodeIds conflict;

    private ResolveSyncWorkResult( Builder builder )
    {
        this.nodePublishRequests = builder.nodePublishRequests;
        this.nodeDeleteRequests = builder.nodeDeleteRequests;
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

    public NodePublishRequests getNodeDeleteRequests()
    {
        return nodeDeleteRequests;
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

        private final NodePublishRequests nodeDeleteRequests = new NodePublishRequests();

        private final Set<NodeId> delete = Sets.newHashSet();

        private final Set<NodeId> conflict = Sets.newHashSet();

        private Builder()
        {
        }

        public Builder publishRequested( final NodeId nodeId, final NodeId initialReasonNodeId )
        {
            this.nodePublishRequests.add( NodePublishRequest.requested( nodeId, initialReasonNodeId ) );
            return this;
        }

        public Builder publishParentFor( final NodeId nodeId, final NodeId parentOf, final NodeId initialReasonNodeId )
        {
            this.nodePublishRequests.add( NodePublishRequest.parentFor( nodeId, parentOf, initialReasonNodeId ) );
            return this;
        }

        public Builder publishChildOf( final NodeId nodeId, final NodeId childOf, final NodeId initialReasonNodeId )
        {
            this.nodePublishRequests.add( NodePublishRequest.childOf( nodeId, childOf, initialReasonNodeId ) );
            return this;
        }

        public Builder publishReferredFrom( final NodeId nodeId, final NodeId referredFrom, final NodeId initialReasonNodeId )
        {
            this.nodePublishRequests.add( NodePublishRequest.referredFrom( nodeId, referredFrom, initialReasonNodeId ) );
            return this;
        }

        public Builder deleteRequested( final NodeId nodeId, final NodeId initialReasonNodeId )
        {
            this.delete.add( nodeId );
            this.nodeDeleteRequests.add( NodePublishRequest.requested( nodeId, initialReasonNodeId ) );
            return this;
        }

        public Builder deleteParentFor( final NodeId nodeId, final NodeId parentOf, final NodeId initialReasonNodeId )
        {
            this.delete.add( nodeId );
            this.nodeDeleteRequests.add( NodePublishRequest.parentFor( nodeId, parentOf, initialReasonNodeId ) );
            return this;
        }

        public Builder deleteChildOf( final NodeId nodeId, final NodeId childOf, final NodeId initialReasonNodeId )
        {
            this.delete.add( nodeId );
            this.nodeDeleteRequests.add( NodePublishRequest.childOf( nodeId, childOf, initialReasonNodeId ) );
            return this;
        }

        public Builder deleteReferredFrom( final NodeId nodeId, final NodeId referredFrom, final NodeId initialReasonNodeId )
        {
            this.delete.add( nodeId );
            this.nodeDeleteRequests.add( NodePublishRequest.referredFrom( nodeId, referredFrom, initialReasonNodeId ) );
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
