package com.enonic.xp.repo.impl.branch;

import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.branch.storage.BranchNodeVersion;

public class MoveBranchDocument
{
    private final NodeVersion nodeVersion;

    private final BranchNodeVersion branchNodeVersion;

    private final NodePath previousPath;

    private MoveBranchDocument( Builder builder )
    {
        nodeVersion = builder.nodeVersion;
        branchNodeVersion = builder.branchNodeVersion;
        previousPath = builder.previousPath;
    }

    public NodeVersion getNodeVersion()
    {
        return nodeVersion;
    }

    public BranchNodeVersion getBranchNodeVersion()
    {
        return branchNodeVersion;
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

        private BranchNodeVersion branchNodeVersion;

        private NodePath previousPath;

        private Builder()
        {
        }

        public Builder nodeVersion( NodeVersion nodeVersion )
        {
            this.nodeVersion = nodeVersion;
            return this;
        }

        public Builder branchNodeVersion( BranchNodeVersion branchNodeVersion )
        {
            this.branchNodeVersion = branchNodeVersion;
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

