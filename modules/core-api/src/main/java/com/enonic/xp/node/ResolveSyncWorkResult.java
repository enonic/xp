package com.enonic.xp.node;

import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.Sets;

@Beta
public class ResolveSyncWorkResult
{
    private final NodePublishRequests nodePublishRequests;

    private final NodePublishRequests nodeDeleteRequests;

    private final NodeId initialReasonNodeId;

    private final NodeIds conflict;

    private ResolveSyncWorkResult( Builder builder )
    {
        this.nodePublishRequests = builder.nodePublishRequests;
        this.nodeDeleteRequests = builder.nodeDeleteRequests;
        this.conflict = NodeIds.from( builder.conflict );
        this.initialReasonNodeId = builder.initialReasonNodeId;
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

    NodeIds getConflict()
    {
        return conflict;
    }

    public NodeId getInitialReasonNodeId()
    {
        return initialReasonNodeId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final NodePublishRequests nodePublishRequests = new NodePublishRequests();

        private final NodePublishRequests nodeDeleteRequests = new NodePublishRequests();

        private final Set<NodeId> conflict = Sets.newHashSet();

        private NodeId initialReasonNodeId;

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

        public Builder publishChildOf( final NodeId nodeId, final NodeId childOf )
        {
            this.nodePublishRequests.add( NodePublishRequest.childOf( nodeId, childOf ) );
            return this;
        }

        public Builder publishReferredFrom( final NodeId nodeId, final NodeId referredFrom )
        {
            this.nodePublishRequests.add( NodePublishRequest.referredFrom( nodeId, referredFrom ) );
            return this;
        }

        public Builder deleteRequested( final NodeId nodeId )
        {
            this.nodeDeleteRequests.add( NodePublishRequest.requested( nodeId ) );
            return this;
        }

        public Builder deleteParentFor( final NodeId nodeId, final NodeId parentOf )
        {
            this.nodeDeleteRequests.add( NodePublishRequest.parentFor( nodeId, parentOf ) );
            return this;
        }

        public Builder deleteChildOf( final NodeId nodeId, final NodeId childOf )
        {
            this.nodeDeleteRequests.add( NodePublishRequest.childOf( nodeId, childOf ) );
            return this;
        }

        public Builder deleteReferredFrom( final NodeId nodeId, final NodeId referredFrom )
        {
            this.nodeDeleteRequests.add( NodePublishRequest.referredFrom( nodeId, referredFrom ) );
            return this;
        }

        public Builder conflict( final NodeId nodeId )
        {
            this.conflict.add( nodeId );
            return this;
        }

        public Builder setInitialReasonNodeId( final NodeId initialReasonNodeId )
        {
            this.initialReasonNodeId = initialReasonNodeId;
            return this;
        }

        public ResolveSyncWorkResult build()
        {
            return new ResolveSyncWorkResult( this );
        }
    }
}
