package com.enonic.xp.repo.impl.storage;

import java.time.Instant;

import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.security.PrincipalKey;

public final class StoreNodeCommitParams
{
    private final NodeCommitId nodeCommitId;

    private final String message;

    private final PrincipalKey committer;

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

    public PrincipalKey getCommitter()
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

        private PrincipalKey committer;

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

        public Builder committer( final PrincipalKey val )
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
