package com.enonic.xp.repo.impl.node;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.PushNodeEntries;
import com.enonic.xp.node.PushNodeEntry;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.repository.RepositoryId;

public class InternalPushNodesResult
    extends PushNodesResult
{
    private final PushNodeEntries pushNodeEntries;

    protected InternalPushNodesResult( Builder builder )
    {
        super( builder );
        pushNodeEntries = builder.pushNodeEntries.build();
    }

    public PushNodeEntries getPushNodeEntries()
    {
        return pushNodeEntries;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends PushNodesResult.Builder<Builder>
    {
        private final PushNodeEntries.Builder pushNodeEntries = PushNodeEntries.create();

        private Builder()
        {
        }

        public Builder addSuccess( final NodeBranchEntry nodeBranchEntry, NodePath currentTargetPath )
        {
            addSuccess( nodeBranchEntry );
            pushNodeEntries.add( PushNodeEntry.create().
                nodeBranchEntry( nodeBranchEntry ).
                currentTargetPath( currentTargetPath ).
                build() );
            return this;
        }

        public Builder targetBranch( final Branch val )
        {
            pushNodeEntries.targetBranch( val );
            return this;
        }

        public Builder targetRepo( final RepositoryId val )
        {
            pushNodeEntries.targetRepo(val);
            return this;
        }

        @Override
        public InternalPushNodesResult build()
        {
            return new InternalPushNodesResult( this );
        }
    }
}
