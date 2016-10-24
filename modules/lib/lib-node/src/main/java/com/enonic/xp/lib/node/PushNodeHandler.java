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
    extends BaseNodeHandler
{
    private NodeIds keys;

    private Branch targetBranch;

    private boolean resolve = false;

    private boolean includeChildren = true;

    private NodeIds exclude;

    @Override
    protected Object doExecute()
    {

        final NodeIds.Builder toBePushed = NodeIds.create();
        final NodeIds.Builder toBeDeleted = NodeIds.create();

        if ( resolve )
        {
            doResolve( toBePushed, toBeDeleted );
        }
        else
        {
            toBePushed.addAll( keys );
        }

        final PushNodesResult push = this.nodeService.push( keys, targetBranch );

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
        for ( final NodeId nodeId : keys )
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

    public void setKeys( final String[] keys )
    {
        this.keys = NodeIds.from( keys );
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
}
