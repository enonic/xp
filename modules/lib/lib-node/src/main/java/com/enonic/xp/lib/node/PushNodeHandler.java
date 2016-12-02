package com.enonic.xp.lib.node;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.lib.node.mapper.PushNodesResultMapper;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.SyncWorkResolverParams;

@SuppressWarnings("unused")
public final class PushNodeHandler
    extends AbstractNodeHandler
{
    private NodeIds nodeIds;

    private Branch targetBranch;

    private boolean resolve = false;

    private boolean includeChildren = true;

    private NodeIds exclude;

    private PushNodeHandler( final Builder builder )
    {
        super( builder );
        nodeIds = getNodeIds( builder.keys );
        targetBranch = builder.targetBranch;
        setResolve( builder.resolve );
        setIncludeChildren( builder.includeChildren );
        exclude = builder.exclude;
    }

    public Object execute()
    {
        final NodeIds.Builder toBePushed = NodeIds.create();
        final NodeIds.Builder toBeDeleted = NodeIds.create();

        if ( resolve )
        {
            doResolve( toBePushed, toBeDeleted );
        }
        else
        {
            toBePushed.addAll( nodeIds );
        }

        final PushNodesResult push = this.nodeService.push( nodeIds, targetBranch );

        final NodeIds deletedNodes = doDelete( toBeDeleted );

        return new PushNodesResultMapper( push, deletedNodes );
    }

    private NodeIds doDelete( final NodeIds.Builder toBeDeleted )
    {
        final NodeIds.Builder builder = NodeIds.create();

        for ( final NodeId node : toBeDeleted.build() )
        {
            builder.addAll( this.nodeService.deleteById( node ) );
        }

        return builder.build();
    }

    private void doResolve( final NodeIds.Builder toBePushed, final NodeIds.Builder toBeDeleted )
    {
        for ( final NodeId nodeId : nodeIds )
        {
            final ResolveSyncWorkResult result = this.nodeService.resolveSyncWork( SyncWorkResolverParams.create().
                nodeId( nodeId ).
                branch( targetBranch ).
                excludedNodeIds( exclude ).
                includeChildren( includeChildren ).
                build() );

            result.getNodeComparisons().forEach( ( nodeComparison -> {
                if ( nodeComparison.getCompareStatus().equals( CompareStatus.PENDING_DELETE ) )
                {
                    toBeDeleted.add( nodeComparison.getNodeId() );
                }
                else
                {

                    toBePushed.add( nodeComparison.getNodeId() );
                }
            } ) );
        }
    }

    public void setNodeIds( final String[] nodeIds )
    {
        this.nodeIds = NodeIds.from( nodeIds );
    }

    public void setTargetBranch( final String targetBranch )
    {
        this.targetBranch = Branch.from( targetBranch );
    }

    public void setResolve( final boolean resolve )
    {
        this.resolve = resolve;
    }

    public void setIncludeChildren( final boolean includeChildren )
    {
        this.includeChildren = includeChildren;
    }

    public void setExclude( final String[] exclude )
    {
        this.exclude = NodeIds.from( exclude );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private NodeKeys keys;

        private Branch targetBranch;

        private boolean resolve;

        private boolean includeChildren;

        private NodeIds exclude;

        private Builder()
        {
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

        public Builder exclude( final NodeIds val )
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
