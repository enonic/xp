package com.enonic.wem.api.node;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.content.CompareStatus;

public class ResolveSyncWorkResult
{
    private final NodesToPublish nodesToPublish;

    private final NodeIds delete;

    private final NodeIds conflict;

    private ResolveSyncWorkResult( Builder builder )
    {
        this.nodesToPublish = NodesToPublish.from( builder.nodesToPublish );
        this.delete = NodeIds.from( builder.delete );
        this.conflict = NodeIds.from( builder.conflict );
    }

    public boolean hasConflicts()
    {
        return getConflict().isNotEmpty();
    }

    public NodesToPublish getNodesToPublish()
    {
        return nodesToPublish;
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

    private class Conflict
    {
        private NodeId nodeId;

        private CompareStatus.Status conflict;
    }

    public static class NodesToPublish
        implements Iterable<NodeToPublish>
    {
        private Set<NodeToPublish> nodesToPublish;

        public NodesToPublish( final Set<NodeToPublish> nodesToPublish )
        {
            this.nodesToPublish = nodesToPublish;
        }

        @Override
        public Iterator<NodeToPublish> iterator()
        {
            return nodesToPublish.iterator();
        }

        public Set<NodeId> getNodeIds()
        {
            final Set<NodeId> nodeIds = Sets.newHashSet();

            for ( final NodeToPublish nodeToPublish : this.nodesToPublish )
            {
                nodeIds.add( nodeToPublish.getNodeId() );
            }

            return nodeIds;
        }

        public static NodesToPublish from( final Set<NodeToPublish> nodesToPublish )
        {
            return new NodesToPublish( nodesToPublish );
        }

        public NodeToPublish get( final NodeId nodeId )
        {
            for ( final NodeToPublish nodeToPublish : this.nodesToPublish )
            {
                if ( nodeToPublish.nodeId.equals( nodeId ) )
                {
                    return nodeToPublish;
                }
            }

            return null;
        }

        public int size()
        {
            return this.nodesToPublish.size();
        }
    }

    public static class NodeToPublish
    {
        private NodeId nodeId;

        private Reason reason;

        private NodeToPublish( final Reason reason, final NodeId nodeId )
        {
            this.reason = reason;
            this.nodeId = nodeId;
        }

        public boolean isParentFor()
        {
            return reason instanceof ParentFor;
        }

        public boolean isReferredFrom()
        {
            return reason instanceof ReferredFrom;
        }

        public static NodeToPublish requested( final NodeId nodeId )
        {
            return new NodeToPublish( new Requested(), nodeId );
        }

        public static NodeToPublish parentFor( final NodeId nodeId, final NodeId parentOf )
        {
            return new NodeToPublish( new ParentFor( parentOf ), nodeId );
        }

        public static NodeToPublish referredFrom( final NodeId nodeId, final NodeId referredFrom )
        {
            return new NodeToPublish( new ReferredFrom( referredFrom ), nodeId );
        }

        public NodeId getNodeId()
        {
            return nodeId;
        }

        public Reason getReason()
        {
            return reason;
        }
    }

    public abstract static class Reason
    {
        public abstract String getMessage();
    }

    public static class Requested
        extends Reason
    {
        @Override
        public String getMessage()
        {
            return "";
        }
    }

    public static class ParentFor
        extends Reason
    {
        private final String message = "Parent for %s";

        private final NodeId nodeId;

        public ParentFor( final NodeId nodeId )
        {
            this.nodeId = nodeId;
        }

        @Override
        public String getMessage()
        {
            return String.format( message, nodeId.toString() );
        }
    }

    public static class ReferredFrom
        extends Reason
    {
        private final String message = "Referred from %s";

        private final NodeId nodeId;

        public ReferredFrom( final NodeId nodeId )
        {
            this.nodeId = nodeId;
        }

        @Override
        public String getMessage()
        {
            return String.format( message, nodeId.toString() );
        }
    }


    public static final class Builder
    {
        private Set<NodeToPublish> nodesToPublish = Sets.newHashSet();

        private Set<NodeId> delete = Sets.newHashSet();

        private Set<NodeId> conflict = Sets.newHashSet();

        private Builder()
        {
        }

        public Builder publish( final NodeToPublish nodeToPublish )
        {
            this.nodesToPublish.add( nodeToPublish );
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
