package com.enonic.xp.node;

import java.time.Instant;

import com.google.common.annotations.Beta;

@Beta
public class NodeCommitEntry
    implements Comparable<NodeCommitEntry>
{
    private final NodeCommitId nodeCommitId;

    private final String message;

    private final Instant timestamp;

    private final String committer;

    private NodeCommitEntry( Builder builder )
    {
        nodeCommitId = builder.nodeCommitId;
        message = builder.message;
        timestamp = builder.timestamp;
        committer = builder.committer;
    }

    public static Builder create()
    {
        return new Builder();
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

    public String getCommitter()
    {
        return committer;
    }

    // Insert with newest first
    @Override
    public int compareTo( final NodeCommitEntry o )
    {
        if ( this.timestamp == o.timestamp )
        {
            return 0;
        }

        if ( this.timestamp.isBefore( o.timestamp ) )
        {
            return 1;
        }

        return -1;
    }

    public static final class Builder
    {
        private NodeCommitId nodeCommitId;

        private String message;

        private Instant timestamp;

        private String committer;

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

        public Builder committer( final String committer )
        {
            this.committer = committer;
            return this;
        }

        public NodeCommitEntry build()
        {
            return new NodeCommitEntry( this );
        }
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final NodeCommitEntry that = (NodeCommitEntry) o;

        if ( nodeCommitId != null ? !nodeCommitId.equals( that.nodeCommitId ) : that.nodeCommitId != null )
        {
            return false;
        }
        if ( message != null ? !message.equals( that.message ) : that.message != null )
        {
            return false;
        }
        if ( timestamp != null ? !timestamp.equals( that.timestamp ) : that.timestamp != null )
        {
            return false;
        }
        return !( committer != null ? !committer.equals( that.committer ) : that.committer != null );
    }

    @Override
    public int hashCode()
    {
        int result = nodeCommitId != null ? nodeCommitId.hashCode() : 0;
        result = 31 * result + ( message != null ? message.hashCode() : 0 );
        result = 31 * result + ( timestamp != null ? timestamp.hashCode() : 0 );
        result = 31 * result + ( committer != null ? committer.hashCode() : 0 );
        return result;
    }
}
