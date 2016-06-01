package com.enonic.xp.node;

public class PublishNodeEntry
{
    private NodeBranchEntry nodeBranchEntry;

    private NodeVersionId nodeVersionId;

    private PublishNodeEntry( final Builder builder )
    {
        nodeVersionId = builder.nodeVersionId;
        nodeBranchEntry = builder.nodeBranchEntry;
    }

    public NodeBranchEntry getNodeBranchEntry()
    {
        return nodeBranchEntry;
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private NodeVersionId nodeVersionId;

        private NodeBranchEntry nodeBranchEntry;

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

        public PublishNodeEntry build()
        {
            return new PublishNodeEntry( this );
        }
    }
}
