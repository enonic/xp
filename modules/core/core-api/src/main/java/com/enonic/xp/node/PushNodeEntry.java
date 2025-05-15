package com.enonic.xp.node;

public final class PushNodeEntry
{
    private final NodeBranchEntry nodeBranchEntry;

    private final NodePath currentTargetPath;

    private PushNodeEntry( final Builder builder )
    {
        nodeBranchEntry = builder.nodeBranchEntry;
        currentTargetPath = builder.currentTargetPath;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeBranchEntry getNodeBranchEntry()
    {
        return nodeBranchEntry;
    }

    public NodePath getCurrentTargetPath()
    {
        return currentTargetPath;
    }

    public static final class Builder
    {
        private NodeBranchEntry nodeBranchEntry;

        private NodePath currentTargetPath;

        private Builder()
        {
        }

        public Builder nodeBranchEntry( final NodeBranchEntry val )
        {
            nodeBranchEntry = val;
            return this;
        }

        public Builder currentTargetPath( final NodePath val )
        {
            currentTargetPath = val;
            return this;
        }

        public PushNodeEntry build()
        {
            return new PushNodeEntry( this );
        }
    }
}
