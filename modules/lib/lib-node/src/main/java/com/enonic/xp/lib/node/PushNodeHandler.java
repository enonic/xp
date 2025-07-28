package com.enonic.xp.lib.node;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.lib.node.mapper.PushNodesResultMapper;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.SyncWorkResolverParams;

@SuppressWarnings("unused")
public final class PushNodeHandler
    extends AbstractNodeHandler
{
    private final NodeKey nodeKey;

    private final NodeKeys nodeKeys;

    private final Branch targetBranch;

    private final boolean resolve;

    private final boolean includeChildren;

    private final NodeKeys exclude;

    private PushNodeHandler( final Builder builder )
    {
        super( builder );
        this.nodeKey = builder.key;
        this.nodeKeys = builder.keys;
        this.targetBranch = builder.targetBranch;
        this.resolve = builder.resolve;
        this.includeChildren = builder.includeChildren;
        this.exclude = builder.exclude;
    }

    @Override
    public Object execute()
    {
        final NodeIds.Builder toBePushed = NodeIds.create();
        final NodeIds nodeIds = getNodeIds();

        if ( resolve )
        {
            doResolve( nodeIds, toBePushed );
        }
        else
        {
            toBePushed.addAll( getNodeIds() );
        }

        final PushNodesResult push = this.nodeService.push( toBePushed.build(), targetBranch );

        return new PushNodesResultMapper( push );
    }

    private NodeIds getNodeIds()
    {
        if ( this.nodeKey != null )
        {
            final NodeId nodeId = getNodeId( this.nodeKey );
            if ( nodeId == null )
            {
                throw new NodeNotFoundException( "Cannot publish node with key [" + this.nodeKey + "]" );
            }
            return NodeIds.from( nodeId );
        }
        else
        {
            return getNodeIds( this.nodeKeys );
        }
    }

    private void doResolve( final NodeIds nodeIds, final NodeIds.Builder toBePushed )
    {
        for ( final NodeId nodeId : nodeIds )
        {
            final ResolveSyncWorkResult result = this.nodeService.resolveSyncWork( SyncWorkResolverParams.create()
                                                                                       .nodeId( nodeId )
                                                                                       .branch( targetBranch )
                                                                                       .excludedNodeIds( getNodeIds( exclude ) )
                                                                                       .includeChildren( includeChildren )
                                                                                       .build() );

            result.getNodeComparisons().forEach( nodeComparison -> toBePushed.add( nodeComparison.getNodeId() ) );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private NodeKey key;

        private NodeKeys keys;

        private Branch targetBranch;

        private boolean resolve;

        private boolean includeChildren;

        private NodeKeys exclude;

        private Builder()
        {
        }

        public Builder key( final NodeKey val )
        {
            key = val;
            return this;
        }

        public Builder keys( final NodeKeys val )
        {
            keys = val;
            return this;
        }

        public Builder targetBranch( final Branch val )
        {
            targetBranch = val;
            return this;
        }

        public Builder resolve( final boolean val )
        {
            resolve = val;
            return this;
        }

        public Builder includeChildren( final boolean val )
        {
            includeChildren = val;
            return this;
        }

        public Builder exclude( final NodeKeys val )
        {
            exclude = val;
            return this;
        }

        public PushNodeHandler build()
        {
            return new PushNodeHandler( this );
        }
    }
}
