package com.enonic.xp.node;

public final class CommitNodeParams
{
    private final NodeCommitEntry nodeCommitEntry;

    private final NodeVersionIds nodeVersionIds;

    private CommitNodeParams( final Builder builder )
    {
        this.nodeCommitEntry = builder.nodeCommitEntry;
        this.nodeVersionIds = builder.nodeVersionIds;
    }

    public NodeCommitEntry getNodeCommitEntry()
    {
        return nodeCommitEntry;
    }

    public NodeVersionIds getNodeVersionIds()
    {
        return nodeVersionIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private NodeCommitEntry nodeCommitEntry;

        private NodeVersionIds nodeVersionIds;

        private Builder()
        {
        }

        public Builder nodeCommitEntry( final NodeCommitEntry nodeCommitEntry )
        {
            this.nodeCommitEntry = nodeCommitEntry;
            return this;
        }

        public Builder nodeVersionIds( final NodeVersionIds nodeVersionIds )
        {
            this.nodeVersionIds = nodeVersionIds;
            return this;
        }

        public CommitNodeParams build()
        {
            return new CommitNodeParams( this );
        }
    }
}
