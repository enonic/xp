package com.enonic.xp.repo.impl.branch.storage;

import com.enonic.xp.node.NodePath;

public class MoveBranchParams
{
    private final NodeBranchMetadata nodeBranchMetadata;

    private final NodePath previousPath;

    private MoveBranchParams( Builder builder )
    {
        nodeBranchMetadata = builder.nodeBranchMetadata;
        previousPath = builder.previousPath;
    }

    public NodeBranchMetadata getNodeBranchMetadata()
    {
        return nodeBranchMetadata;
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
        private NodeBranchMetadata nodeBranchMetadata;

        private NodePath previousPath;

        private Builder()
        {
        }

        public Builder branchNodeVersion( NodeBranchMetadata nodeBranchMetadata )
        {
            this.nodeBranchMetadata = nodeBranchMetadata;
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

