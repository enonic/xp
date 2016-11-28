package com.enonic.xp.lib.node;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.lib.node.mapper.ResolveSyncWorkResultMapper;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.SyncWorkResolverParams;

@SuppressWarnings("unused")
public final class DiffBranchesHandler
    extends BaseNodeHandler
{
    private NodeId nodeId;

    private Branch targetBranch;

    private boolean includeChildren = true;

    private DiffBranchesHandler( final Builder builder )
    {
        super( builder );
        nodeId = builder.nodeId;
        targetBranch = builder.targetBranch;
        setIncludeChildren( builder.includeChildren );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Object execute()
    {
        final ResolveSyncWorkResult result = this.nodeService.resolveSyncWork( SyncWorkResolverParams.create().
            includeChildren( includeChildren ).
            nodeId( nodeId ).
            branch( targetBranch ).
            build() );

        return new ResolveSyncWorkResultMapper( result );
    }

    @SuppressWarnings("unused")
    public void setNodeId( final String nodeId )
    {
        this.nodeId = NodeId.from( nodeId );
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

    public static final class Builder
        extends BaseNodeHandler.Builder<Builder>
    {
        private NodeId nodeId;

        private Branch targetBranch;

        private boolean includeChildren;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId val )
        {
            nodeId = val;
            return this;
        }

        public Builder targetBranch( final Branch val )
        {
            targetBranch = val;
            return this;
        }

        public Builder includeChildren( final boolean val )
        {
            includeChildren = val;
            return this;
        }

        public DiffBranchesHandler build()
        {
            return new DiffBranchesHandler( this );
        }
    }
}
