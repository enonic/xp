package com.enonic.xp.node;

import com.enonic.xp.branch.Branch;

public class PublishNodeEntry
{
    private NodeBranchEntry nodeBranchEntry;

    private NodeVersionId nodeVersionId;

    private Branch target;

    private PublishNodeEntry( final Builder builder )
    {
        target = builder.target;
        nodeVersionId = builder.nodeVersionId;
        nodeBranchEntry = builder.nodeBranchEntry;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Branch target;

        private NodeVersionId nodeVersionId;

        private NodeBranchEntry nodeBranchEntry;

        private Builder()
        {
        }

        public Builder target( final Branch val )
        {
            target = val;
            return this;
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
