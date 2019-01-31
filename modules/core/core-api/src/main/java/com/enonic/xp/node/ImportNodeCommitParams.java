package com.enonic.xp.node;

import java.time.Instant;

public class ImportNodeCommitParams
{
    private final NodeCommitId nodeCommitId;

    private final String message;

    private final String committer;

    private final Instant timestamp;

    private ImportNodeCommitParams( final Builder builder )
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

        public Builder committer( final String committer )
        {
            this.committer = committer;
            return this;
        }

        public Builder timestamp( final Instant timestamp )
        {
            this.timestamp = timestamp;
            return this;
        }

        public ImportNodeCommitParams build()
        {
            return new ImportNodeCommitParams( this );
        }
    }
}
