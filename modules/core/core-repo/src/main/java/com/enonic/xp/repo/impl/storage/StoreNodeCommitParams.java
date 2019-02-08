package com.enonic.xp.repo.impl.storage;

import java.time.Instant;

import com.enonic.xp.node.NodeCommitId;

public class StoreNodeCommitParams
{
    private final NodeCommitId nodeCommitId;

    private final String message;

    private final String committer;

    private final Instant timestamp;

    private StoreNodeCommitParams( final Builder builder )
    {
        nodeCommitId = builder.nodeCommitId;
        message = builder.message;
        committer = builder.committer;
        timestamp = builder.timestamp;
    }

    public NodeCommitId getNodeCommitId()
    {
        return nodeCommitId;
    }

    public String getMessage()
    {
        return message;
    }

    public String getCommitter()
    {
        return committer;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private NodeCommitId nodeCommitId;

        private String message;

        private String committer;

        private Instant timestamp;

        private Builder()
        {
        }

        public Builder nodeCommitId( final NodeCommitId val )
        {
            nodeCommitId = val;
            return this;
        }

        public Builder message( final String val )
        {
            message = val;
            return this;
        }

        public Builder committer( final String val )
        {
            committer = val;
            return this;
        }

        public Builder timestamp( final Instant val )
        {
            timestamp = val;
            return this;
        }

        public StoreNodeCommitParams build()
        {
            return new StoreNodeCommitParams( this );
        }
    }
}
