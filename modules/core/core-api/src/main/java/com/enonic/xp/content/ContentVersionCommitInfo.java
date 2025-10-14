package com.enonic.xp.content;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.security.PrincipalKey;

public final class ContentVersionCommitInfo
{
    private final PrincipalKey publisher;

    private final Instant timestamp;

    private final String message;

    private final CommitType type;

    private ContentVersionCommitInfo( Builder builder )
    {
        publisher = builder.publisher;
        timestamp = builder.timestamp;
        message = builder.message;
        type = builder.type;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public PrincipalKey getPublisher()
    {
        return publisher;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public String getMessage()
    {
        return message;
    }

    public CommitType getType()
    {
        return type;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ContentVersionCommitInfo ) )
        {
            return false;
        }
        final ContentVersionCommitInfo that = (ContentVersionCommitInfo) o;
        return Objects.equals( publisher, that.publisher ) && Objects.equals( timestamp, that.timestamp ) &&
            Objects.equals( message, that.message ) &&
            Objects.equals( type, that.type );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( publisher, timestamp, message );
    }

    public enum CommitType
    {
        PUBLISHED, UNPUBLISHED, ARCHIVED, RESTORED, CUSTOM
    }

    public static final class Builder
    {
        private PrincipalKey publisher;

        private Instant timestamp;

        private String message;

        private CommitType type;

        private Builder()
        {
        }

        public Builder publisher( final PrincipalKey publisher )
        {
            this.publisher = publisher;
            return this;
        }

        public Builder timestamp( final Instant timestamp )
        {
            this.timestamp = timestamp;
            return this;
        }

        public Builder message( final String message )
        {
            this.message = message;
            return this;
        }

        public Builder type( final CommitType type )
        {
            this.type = type;
            return this;
        }

        public ContentVersionCommitInfo build()
        {
            return new ContentVersionCommitInfo( this );
        }
    }
}
