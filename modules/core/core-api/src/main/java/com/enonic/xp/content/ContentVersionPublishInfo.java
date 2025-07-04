package com.enonic.xp.content;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.security.PrincipalKey;

public final class ContentVersionPublishInfo
{
    private final PrincipalKey publisher;

    private final Instant timestamp;

    private final String message;

    private final CommitType type;

    private final ContentPublishInfo contentPublishInfo;

    private ContentVersionPublishInfo( Builder builder )
    {
        publisher = builder.publisher;
        timestamp = builder.timestamp;
        message = builder.message;
        contentPublishInfo = builder.contentPublishInfo;
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

    public ContentPublishInfo getContentPublishInfo()
    {
        return contentPublishInfo;
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
        if ( !( o instanceof ContentVersionPublishInfo ) )
        {
            return false;
        }
        final ContentVersionPublishInfo that = (ContentVersionPublishInfo) o;
        return Objects.equals( publisher, that.publisher ) && Objects.equals( timestamp, that.timestamp ) &&
            Objects.equals( message, that.message ) && Objects.equals( contentPublishInfo, that.contentPublishInfo ) &&
            Objects.equals( type, that.type );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( publisher, timestamp, message, contentPublishInfo );
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

        private ContentPublishInfo contentPublishInfo;

        private CommitType type;

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

        public Builder contentPublishInfo( final ContentPublishInfo contentPublishInfo )
        {
            this.contentPublishInfo = contentPublishInfo;
            return this;
        }

        public Builder type( final CommitType type )
        {
            this.type = type;
            return this;
        }

        public ContentVersionPublishInfo build()
        {
            return new ContentVersionPublishInfo( this );
        }
    }
}
