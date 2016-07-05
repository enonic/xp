package com.enonic.xp.repo.impl.branch.storage;

import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodePath;

public class MoveBranchParams
{
    private final NodeBranchEntry nodeBranchEntry;

    private final NodePath previousPath;

    private MoveBranchParams( Builder builder )
    {
        nodeBranchEntry = builder.nodeBranchEntry;
        previousPath = builder.previousPath;
    }

    public NodeBranchEntry getNodeBranchEntry()
    {
        return nodeBranchEntry;
    }

    public NodePath getPreviousPath()
    {
        return previousPath;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodeBranchEntry nodeBranchEntry;

        private NodePath previousPath;

        private Builder()
        {
        }

        public Builder branchNodeVersion( NodeBranchEntry nodeBranchEntry )
        {
            this.nodeBranchEntry = nodeBranchEntry;
            return this;
        }

        public Builder previousPath( NodePath previousPath )
        {
            this.previousPath = previousPath;
            return this;
        }

        public MoveBranchParams build()
        {
            return new MoveBranchParams( this );
        }
    }
}

