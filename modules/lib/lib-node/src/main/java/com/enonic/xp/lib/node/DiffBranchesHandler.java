package com.enonic.xp.lib.node;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.lib.node.mapper.ResolveSyncWorkResultMapper;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.SyncWorkResolverParams;

@SuppressWarnings("unused")
public final class DiffBranchesHandler
    extends OldBaseNodeHandler
{
    private NodeId key;

    private Branch targetBranch;

    private boolean includeChildren = true;

    @Override
    protected Object doExecute()
    {
        final ResolveSyncWorkResult result = this.nodeService.resolveSyncWork( SyncWorkResolverParams.create().
            includeChildren( includeChildren ).
            nodeId( key ).
            branch( targetBranch ).
            build() );

        return new ResolveSyncWorkResultMapper( result );
    }

    @SuppressWarnings("unused")
    public void setKey( final String key )
    {
        this.key = NodeId.from( key );
    }

    @SuppressWarnings("unused")
    public void setTargetBranch( final String targetBranch )
    {
        this.targetBranch = Branch.from( targetBranch );
    }

    @SuppressWarnings("unused")
    public void setIncludeChildren( final boolean includeChildren )
    {
        this.includeChildren = includeChildren;
    }
}
