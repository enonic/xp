package com.enonic.xp.lib.node;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.lib.node.mapper.ResolveSyncWorkResultMapper;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.SyncWorkResolverParams;

@SuppressWarnings("unused")
public final class DiffBranchesHandler
    extends AbstractNodeHandler
{
    private NodeKey nodeKey;

    private Branch targetBranch;

    private boolean includeChildren = true;

    private DiffBranchesHandler( final Builder builder )
    {
        super( builder );
        this.nodeKey = builder.nodeKey;
        this.targetBranch = builder.targetBranch;
        this.includeChildren = builder.includeChildren;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Object execute()
    {
        final NodeId nodeId = getNodeId( this.nodeKey );

        if ( nodeId == null )
        {
            throw new NodeNotFoundException( "Node with key [" + this.nodeKey + "] not found" );
        }

        final ResolveSyncWorkResult result = this.nodeService.resolveSyncWork( SyncWorkResolverParams.create().
            includeChildren( includeChildren ).
            nodeId( nodeId ).
            branch( targetBranch ).
            build() );

        return new ResolveSyncWorkResultMapper( result );
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private NodeKey nodeKey;

        private Branch targetBranch;

        private boolean includeChildren;

        private Builder()
        {
        }

        public Builder key( final NodeKey val )
        {
            nodeKey = val;
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
