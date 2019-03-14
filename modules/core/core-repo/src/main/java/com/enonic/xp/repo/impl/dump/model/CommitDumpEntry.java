package com.enonic.xp.repo.impl.dump.model;

import java.time.Instant;

import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.security.PrincipalKey;

public class CommitDumpEntry
{
    private NodeCommitId nodeCommitId;

    private final String message;

    private final Instant timestamp;

    private final PrincipalKey committer;

    private CommitDumpEntry( final Builder builder )
    {
        nodeCommitId = builder.nodeCommitId;
        message = builder.message == null ? "" : builder.message;
        timestamp = builder.timestamp == null ? Instant.now() : builder.timestamp;
        committer = builder.committer == null ? PrincipalKey.ofAnonymous() : builder.committer;
    }

    public NodeCommitId getNodeCommitId()
    {
        return nodeCommitId;
    }

    public String getMessage()
    {
        return message;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public PrincipalKey getCommitter()
    {
        return committer;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodeCommitId nodeCommitId;

        private String message;

        private Instant timestamp;

        private PrincipalKey committer;

        private Builder()
        {
        }

        public Builder nodeCommitId( final NodeCommitId nodeCommitId )
        {
            this.nodeCommitId = nodeCommitId;
            return this;
        }

        public Builder message( final String message )
        {
            this.message = message;
            return this;
        }

        public Builder timestamp( final Instant timestamp )
        {
            this.timestamp = timestamp;
            return this;
        }

        public Builder committer( final PrincipalKey committer )
        {
            this.committer = committer;
            return this;
        }

        public CommitDumpEntry build()
        {
            return new CommitDumpEntry( this );
        }
    }
}
