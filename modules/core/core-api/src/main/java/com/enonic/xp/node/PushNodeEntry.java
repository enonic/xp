package com.enonic.xp.node;

public class PushNodeEntry
{
    private NodeBranchEntry nodeBranchEntry;

    private NodeVersionId nodeVersionId;

    private NodePath previousPath;

    private PushNodeEntry( final Builder builder )
    {
        nodeVersionId = builder.nodeVersionId;
        nodeBranchEntry = builder.nodeBranchEntry;
        previousPath = builder.previousPath;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeBranchEntry getNodeBranchEntry()
    {
        return nodeBranchEntry;
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }

    public NodePath getPreviousPath()
    {
        return previousPath;
    }

    public static final class Builder
    {

        private NodeVersionId nodeVersionId;

        private NodeBranchEntry nodeBranchEntry;

        private NodePath previousPath;

        private Builder()
        {
        }

        public Builder nodeVersionId( final NodeVersionId val )
        {
            nodeVersionId = val;
            return this;
        }

        public Builder nodeBranchEntry( final NodeBranchEntry val )
        {
            nodeBranchEntry = val;
            return this;
        }

        public Builder previousPath( final NodePath val )
        {
            previousPath = val;
            return this;
        }

        public PushNodeEntry build()
        {
            return new PushNodeEntry( this );
        }
    }
}
