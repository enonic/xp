package com.enonic.xp.repo.impl.branch;

import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.branch.storage.NodeBranchMetadata;

public class MoveBranchDocument
{
    private final NodeVersion nodeVersion;

    private final NodeBranchMetadata nodeBranchMetadata;

    private final NodePath previousPath;

    private MoveBranchDocument( Builder builder )
    {
        nodeVersion = builder.nodeVersion;
        nodeBranchMetadata = builder.nodeBranchMetadata;
        previousPath = builder.previousPath;
    }

    public NodeVersion getNodeVersion()
    {
        return nodeVersion;
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
        private NodeVersion nodeVersion;

        private NodeBranchMetadata nodeBranchMetadata;

        private NodePath previousPath;

        private Builder()
        {
        }

        public Builder nodeVersion( NodeVersion nodeVersion )
        {
            this.nodeVersion = nodeVersion;
            return this;
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

        public MoveBranchDocument build()
        {
            return new MoveBranchDocument( this );
        }
    }
}

